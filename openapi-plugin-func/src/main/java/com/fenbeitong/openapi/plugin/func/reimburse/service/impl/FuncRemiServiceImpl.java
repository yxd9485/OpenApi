package com.fenbeitong.openapi.plugin.func.reimburse.service.impl;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.finhub.common.saas.entity.CostAttribution;
import com.fenbeitong.finhub.common.saas.entity.CostAttributionGroup;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.*;
import com.fenbeitong.openapi.plugin.func.reimburse.service.FuncRemiService;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.saasplus.api.model.dto.bill.*;
import com.fenbeitong.saasplus.api.model.dto.common.PageDataCommonResult;
import com.fenbeitong.saasplus.api.model.dto.util.KvStrContract;
import com.fenbeitong.saasplus.api.service.bill.IApplyReimburseBillService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName FuncRemiServiceImpl
 * @Description 保险单管理
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/16 下午9:44
 **/
@Service
@Slf4j
public class FuncRemiServiceImpl implements FuncRemiService {

    @DubboReference(check = false)
    private IApplyReimburseBillService reimburseBillService;
    @Value("${host.saas}")
    private String saasHost;
    @Autowired
    private IEtlService etlService;
    private static final Long REMI_DETAIL_CONFIGID = 2528l;
    @Autowired
    private RestHttpUtils httpUtils;
    @Autowired
    private OpenEmployeeExtServiceImpl employeeExtService;
    @Value("${host.usercenter}")
    private String ucHost;

    public static final String TAX_AMOUNT= "税额";
    public static final String TAX_RATE = "税率";

    /**
     * 查询报销单详情信息
     */
    @Override
    public Object getRemiDetailInfo(RemiDetailReqDTO req, String companyId) throws BindException {
        //列表响应对象
        RemiResultDTO resp = new RemiResultDTO();
        req.setCompanyId(companyId);
        //参数校验
        checkDateFormat(req);
        //1、调用saas获取报销单明细
        PageDataCommonResult<List<ThirdReimburseQueryRes>> reimburseQueryResult = getRemiDetailInfo(req);
        Integer count = NumericUtils.obj2int(reimburseQueryResult.getTotal(), 0);
        resp.setTotalCount(count);
        resp.setPageIndex(req.getPageIndex());
        resp.setPageSize(req.getPageSize());
        resp.setTotalPages((count + req.getPageSize() - 1) / req.getPageSize());
        resp.setReimbForm(Lists.newArrayList());
        if (!ObjectUtils.isEmpty(reimburseQueryResult.getData())) {
            //转换数据
            List<RemiDetailResDTO> remiDetailResList = convertRemiDetail(reimburseQueryResult.getData(), companyId);
            resp.setReimbForm(remiDetailResList);
        }
        return resp;
    }

    /**
     * 批量更新报销单状态
     */
    @Override
    public Object updateRemiStatus(RemiUpdStatusDTO remiUpdStatusDTO) throws BindException {
        //校验参数
        ValidatorUtils.validateBySpring(remiUpdStatusDTO);
        List<String> oldRemiIds = Arrays.asList(remiUpdStatusDTO.getReimbId().split(","));
        List<String> remiIds = oldRemiIds.stream().distinct().collect(Collectors.toList());
        log.info("第三方同步报销单状态调用开始，状态列表:{} ", JsonUtils.toJson(remiIds));
        ThirdBillSyncReqDTO thirdBillSyncReqDTO = new ThirdBillSyncReqDTO();
        thirdBillSyncReqDTO.setApplyIdList(remiIds);
        List<ThirdBillSyncResDTO> thirdBillSyncResDTOS = reimburseBillService.thirdReimburseBillStateSync(thirdBillSyncReqDTO);
        if (ObjectUtils.isEmpty(thirdBillSyncResDTOS)) {
            //无返回时默认为更新成功
            return null;
        }
        //存在更新错误
        log.info("第三方同步报销单状态返回：{}", thirdBillSyncResDTOS);
        List<RemiUpdStatusResDTO> remiUpdStatusResDTOList = new ArrayList<>();
        for (ThirdBillSyncResDTO thirdBillSyncResDTO : thirdBillSyncResDTOS) {
            RemiUpdStatusResDTO remiUpdStatusRes = RemiUpdStatusResDTO.builder().reimbId(thirdBillSyncResDTO.getReim_id()).errorMsg(thirdBillSyncResDTO.getMsg()).build();
            remiUpdStatusResDTOList.add(remiUpdStatusRes);
        }
        return remiUpdStatusResDTOList;
    }

    private PageDataCommonResult<List<ThirdReimburseQueryRes>> getRemiDetailInfo(RemiDetailReqDTO req) throws BindException {
        Integer pageIndex = NumericUtils.obj2int(req.getPageIndex(), 1);
        Integer pageSize = NumericUtils.obj2int(req.getPageSize(), 20);
        req.setPageIndex(pageIndex);
        req.setPageSize(pageSize);
        //校验参数
        ValidatorUtils.validateBySpring(req);
        ThirdReimburseQueryReq thirdReimburseQueryReq = JsonUtils.toObj(JsonUtils.toJson(req), ThirdReimburseQueryReq.class);
        log.info("报销单列表查询开始，thirdReimburseQueryReq:{} ", JsonUtils.toJson(thirdReimburseQueryReq));
        PageDataCommonResult<List<ThirdReimburseQueryRes>> listPageDataCommonResult = reimburseBillService.queryReimburseBillListByPaymentStatus(thirdReimburseQueryReq);
        log.info("报销单接口查询结果：{} ", JsonUtils.toJson(listPageDataCommonResult));
        if (ObjectUtils.isEmpty(listPageDataCommonResult)) {
            log.info("[调用报销单列表查询接口失败]");
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_ERROR));
        }
        return listPageDataCommonResult;
    }

    public UcEmployeeDetailDTO loadUserByFbId(String companyId, String employeeId) {
        Map<String, Object> jsonMap = Maps.newHashMap();
        jsonMap.put("companyId", companyId);
        jsonMap.put("type", 0);
        jsonMap.put("employeeId", employeeId);
        jsonMap.put("userType", 1);
        String result = httpUtils.postJson(ucHost + "/uc/inner/employee/third/operate/info", JsonUtils.toJson(jsonMap));
        Map<String, Object> resultMap = JsonUtils.toObj(result, Map.class);
        return resultMap == null ? null : JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(resultMap, "data")), UcEmployeeDetailDTO.class);
    }

    /**
     * 使用报销单id获取报销单详情
     *
     * @param idList
     * @param companyId
     * @return
     */
    @Override
    public List<RemiDetailResDTO> queryReimburseBillListByIdList(List<String> idList, String companyId) {
        List<ThirdReimburseQueryRes> thirdReimburseQueryRes = reimburseBillService.queryReimburseBillListByIdList(idList, companyId);
        log.info("报销单接口查询结果：{} ", JsonUtils.toJson(thirdReimburseQueryRes));
        List<RemiDetailResDTO> remiDetailResList = convertRemiDetail(thirdReimburseQueryRes, companyId);
        return remiDetailResList;
    }

    /**
     * 数据格式转换
     *
     * @param thirdReimburseQueryResList
     * @param companyId
     */
    public List<RemiDetailResDTO> convertRemiDetail(List<ThirdReimburseQueryRes> thirdReimburseQueryResList, String companyId) {
        List<Map<String, Object>> remiburseResult = JSON.parseObject(JSON.toJSONString(thirdReimburseQueryResList), new com.alibaba.fastjson.TypeReference<List<Map<String, Object>>>() {
        });
        List<Map> transform = etlService.transform(REMI_DETAIL_CONFIGID, remiburseResult);

        Map<String, Object> costInfoMap = new HashMap<>();

        //报销单费用信息赋值
        for (ThirdReimburseQueryRes thirdReimburseQueryRes : thirdReimburseQueryResList) {
            List<RemiCostResDTO> remiCostResDTOList = new ArrayList<>();

            if (!ObjectUtils.isEmpty(thirdReimburseQueryRes.getCost_list())) {
                for (ThirdCostRes costRes : thirdReimburseQueryRes.getCost_list()) {
                    RemiCostResDTO remiCostResDTO = RemiCostResDTO.builder().costCategoryCustomCode(costRes.getCustom_code()).costCategoryCode(costRes.getCost_category_id()).costCategoryName(costRes.getCost_category()).formType(costRes.getType()).totalAmount(costRes.getTotal_amount()).costReason(costRes.getCost_desc()).build();
                    List<CostAttributionGroupDTO> costAttributionGroupList = new ArrayList<>();
                    List<RemiInvoiceResDTO> remiInvoiceResDTOList = new ArrayList<>();
                    List<KVEntity> costCustFields = new ArrayList<>();
                    if (!ObjectUtils.isEmpty(costRes.getForm_custom_fields())) {
                        for (KvStrContract kvStrContract : costRes.getForm_custom_fields()) {
                            KVEntity kvEntity = new KVEntity();
                            kvEntity.setKey(kvStrContract.getKey());
                            kvEntity.setValue(kvStrContract.getValue());
                            costCustFields.add(kvEntity);
                        }
                    }
                    if (!ObjectUtils.isEmpty(costRes.getInvoice_List())) {
                        for (ThirdInvoiceRes invoiceRes : costRes.getInvoice_List()) {
                            RemiInvoiceResDTO remiInvoiceResDTO = new RemiInvoiceResDTO();
                            remiInvoiceResDTO.setInvId(invoiceRes.getFb_inv_id());
                            remiInvoiceResDTO.setInvType(invoiceRes.getInv_type());
                            remiInvoiceResDTO.setInvTypeName(invoiceRes.getInv_type_name());
                            remiInvoiceResDTO.setInvCode(invoiceRes.getInv_code());
                            remiInvoiceResDTO.setIssuedDate(invoiceRes.getIssued_date());
                            remiInvoiceResDTO.setSellerName(invoiceRes.getSeller_name());
                            remiInvoiceResDTO.setBuyerName(invoiceRes.getBuyer_name());
                            remiInvoiceResDTO.setTotalPricePlusTax(invoiceRes.getTotal_price_plus_tax());
                            remiInvoiceResDTO.setInvTaxAmount(invoiceRes.getInv_tax_amount());
                            remiInvoiceResDTO.setTaxRate(invoiceRes.getTax_rate());
                            remiInvoiceResDTO.setExcludeTaxAmount(invoiceRes.getExclude_tax_amount());
                            remiInvoiceResDTO.setInvPdfUrl(invoiceRes.getInv_pdf_url());
                            remiInvoiceResDTO.setInvPicUrl(invoiceRes.getInv_pic_url());
                            remiInvoiceResDTO.setInvNumber(invoiceRes.getInv_num());
                            remiInvoiceResDTO.setSellerTaxNumber(invoiceRes.getSeller_tax_num());
                            remiInvoiceResDTO.setBuyerTaxNumber(invoiceRes.getBuyer_tax_num());

                            List<ThirdInvoiceRes.FbtVATBaseSubDetailDTO> subDetails = invoiceRes.getSubDetails();
                            if (CollectionUtils.isNotBlank(subDetails)) {
                                List<RemiInvoiceResDTO.DetailData> details = Lists.newArrayList();
                                subDetails.forEach(sub -> {
                                     RemiInvoiceResDTO.DetailData build = RemiInvoiceResDTO.DetailData.builder()
                                        .rate(sub.getItemTaxRate())
                                        .amount(sub.getItemTaxAmount())
                                        .name(sub.getItemName())
                                        .build();
                                     details.add(build);
                                });
                                remiInvoiceResDTO.setDetail(details);
                            }
                            remiInvoiceResDTOList.add(remiInvoiceResDTO);
                        }
                    }
                    if (!ObjectUtils.isEmpty(costRes.getCost_attribution_group())) {
                        for (CostAttributionGroup costAttributionGroup : costRes.getCost_attribution_group()) {
                            CostAttributionGroupDTO costAttributeGroupBuild = CostAttributionGroupDTO.builder().category(costAttributionGroup.getCategory()).recordId(costAttributionGroup.getRecordId()).categoryName(costAttributionGroup.getCategoryName()).range(costAttributionGroup.getCostAttributionRange()).build();
                            List<CostAttributionDTO> costAttributionList = new ArrayList<>();
                            if (!ObjectUtils.isEmpty(costAttributionGroup.getCostAttributionList())) {
                                for (CostAttribution costAttribution : costAttributionGroup.getCostAttributionList()) {
                                    CostAttributionDTO costAttributionDTO = CostAttributionDTO.builder().id(costAttribution.getId()).name(costAttribution.getName()).weight(costAttribution.getWeight()).amount(costAttribution.getPrice()).thirdId(costAttribution.getThirdCostAttributionId()).costAttributionCode(costAttribution.getCode()).build();
                                    costAttributionList.add(costAttributionDTO);
                                    costAttributeGroupBuild.setCostAttributionList(costAttributionList);
                                }
                                costAttributionGroupList.add(costAttributeGroupBuild);
                            }
                        }
                    }

                    remiCostResDTO.setCostAttributionGroup(costAttributionGroupList);
                    remiCostResDTO.setReimbInvoice(remiInvoiceResDTOList);
                    remiCostResDTO.setCostCustomFields(costCustFields);


                    List<KvStrContract> formCustomFields = costRes.getForm_custom_fields();
                    if (!ObjectUtils.isEmpty(formCustomFields)) {
                        Map<String, String> collect = formCustomFields.stream().collect(Collectors.toMap(KvStrContract::getKey, KvStrContract::getValue, (key1, key2) -> key2));
                        // 费用增加税额
                        remiCostResDTO.setTaxAmount(collect.get(TAX_AMOUNT));
                        // 费用增加税率
                        remiCostResDTO.setTaxRate(collect.get(TAX_RATE));
                    }
                    remiCostResDTOList.add(remiCostResDTO);
                    costInfoMap.put(thirdReimburseQueryRes.getId(), remiCostResDTOList);
                }
            }
        }
        List<RemiDetailResDTO> remiDetailResList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(transform)) {
            for (Map remiDetailMap : transform) {
                RemiDetailResDTO remiDetailDTO = new RemiDetailResDTO();
                RemiDetailConvertDTO remiDetailResDTO = JSON.parseObject(JsonUtils.toJson(remiDetailMap), RemiDetailConvertDTO.class);
                if (!ObjectUtils.isEmpty(costInfoMap.get(remiDetailResDTO.getReimbId()))) {
                    remiDetailResDTO.setReimbExpense((List<RemiCostResDTO>) costInfoMap.get(remiDetailResDTO.getReimbId()));
                }
                List employeeInfoList = new ArrayList();//人员信息
                //兼容处理，快照中没有通过uc获取
                String ucPropEmployeeNumber = null;
                String ucPropPhoneNumber = null;
                String ucPropOrgUnitId = null;
                String ucPropOrgUnitName = null;
                String ucPropThirdEmployeeId= null;
                String ucPropThirdOrgId = null;
                List<Map<String, Object>> ucPropExtInfoList = null;
                UcEmployeeDetailDTO proposorDetailDTO = loadUserByFbId(companyId, remiDetailResDTO.getProposorId());
                if (!ObjectUtils.isEmpty(proposorDetailDTO) && !ObjectUtils.isEmpty(proposorDetailDTO.getEmployee())) {
                    UcEmployeeDetailDTO.UcEmployeeDetailEmpDTO proposorEmployee = proposorDetailDTO.getEmployee();
                    ucPropEmployeeNumber = proposorEmployee.getEmployee_number();
                    ucPropPhoneNumber = proposorEmployee.getPhone_num();
                    ucPropOrgUnitId = proposorEmployee.getOrg_unit_id();
                    ucPropOrgUnitName = proposorEmployee.getOrg_unit_name();
                    ucPropThirdEmployeeId = proposorEmployee.getThirdEmployeeId();
                    ucPropThirdOrgId= proposorEmployee.getThird_org_id();
                    ucPropExtInfoList = proposorDetailDTO.getEmployee().getExpand_list() == null ? null : (List<Map<String, Object>>) proposorDetailDTO.getEmployee().getExpand_list();
                }

                //提单人用户信息
                EmployeeInfoDTO proposorInfoDTO = new EmployeeInfoDTO();
                proposorInfoDTO.setType(1);
                List<Map<String, Object>> proposorCustomFields = JsonUtils.toObj(remiDetailResDTO.getProposorCustomFieldsStr(), new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
                });
                String proposorEmployeeNo = StringUtils.isBlank(remiDetailResDTO.getEmployeeNumber()) ? ucPropEmployeeNumber:remiDetailResDTO.getEmployeeNumber();
                String proposorEmployeePhone = StringUtils.isBlank(remiDetailResDTO.getProposorPhone())?ucPropPhoneNumber:remiDetailResDTO.getProposorPhone();
                String proposorDepartmentId = StringUtils.isBlank(remiDetailResDTO.getProposorDepartmentId())?ucPropOrgUnitId:remiDetailResDTO.getProposorDepartmentId();
                String proposorDepartmentName = StringUtils.isBlank(remiDetailResDTO.getProposorDepartmentName())?ucPropOrgUnitName:remiDetailResDTO.getProposorDepartmentName();
                String proposorThirdEmployeeId = StringUtils.isBlank(remiDetailResDTO.getThirdEmployeeId())?ucPropThirdEmployeeId:remiDetailResDTO.getThirdEmployeeId();
                String proposorThirdDeptId = StringUtils.isBlank(remiDetailResDTO.getThirdProposorDepartmentId())?ucPropThirdOrgId:remiDetailResDTO.getThirdProposorDepartmentId();
                List<Map<String, Object>> proposorExtInfo = ObjectUtils.isEmpty(proposorCustomFields)?ucPropExtInfoList:proposorCustomFields;

                proposorInfoDTO.setEmployeeNo(proposorEmployeeNo);
                proposorInfoDTO.setThirdEmployeeId(proposorThirdEmployeeId);
                proposorInfoDTO.setEmployeePhone(proposorEmployeePhone);
                proposorInfoDTO.setEmployeeDepartmentId(proposorDepartmentId);
                proposorInfoDTO.setEmployeeDepartmentName(proposorDepartmentName);
                proposorInfoDTO.setThirdDepartmentId(proposorThirdDeptId);

                List<KVEntity> proposorCustomField = new ArrayList<KVEntity>();
                if (!ObjectUtils.isEmpty(proposorExtInfo)) {
                    Map<String, Object> employeeExtInfo = proposorExtInfo.get(0);
                    for (Map.Entry<String, Object> entry : employeeExtInfo.entrySet()) {
                        KVEntity kvEntity = new KVEntity();
                        kvEntity.setKey(entry.getKey());
                        kvEntity.setValue(entry.getValue());
                        proposorCustomField.add(kvEntity);
                    }
                    if (!ObjectUtils.isEmpty(proposorCustomField)) {
                        proposorInfoDTO.setCustomFields(proposorCustomField);
                    }
                }

                employeeInfoList.add(proposorInfoDTO);

                //兼容处理，快照中没有通过uc获取
                String ucUserEmployeeNumber = null;
                String ucUserPhoneNumber = null;
                String ucUserOrgUnitId = null;
                String ucUserOrgUnitName = null;
                String ucUserThirdEmployeeId= null;
                String ucUserThirdOrgId = null;
                List<Map<String, Object>> ucUserExtInfoList = null;
                UcEmployeeDetailDTO userDetailDTO = loadUserByFbId(companyId, remiDetailResDTO.getUserId());
                if (!ObjectUtils.isEmpty(userDetailDTO) && !ObjectUtils.isEmpty(userDetailDTO.getEmployee())) {
                    UcEmployeeDetailDTO.UcEmployeeDetailEmpDTO userEmployee = userDetailDTO.getEmployee();
                    ucUserEmployeeNumber = userEmployee.getEmployee_number();
                    ucUserPhoneNumber = userEmployee.getPhone_num();
                    ucUserOrgUnitId = userEmployee.getOrg_unit_id();
                    ucUserOrgUnitName = userEmployee.getOrg_unit_name();
                    ucUserThirdEmployeeId = userEmployee.getThirdEmployeeId();
                    ucUserThirdOrgId = userEmployee.getThird_org_id();
                    ucUserExtInfoList = userEmployee.getExpand_list() == null ? null : (List<Map<String, Object>>) userEmployee.getExpand_list();
                }

                EmployeeInfoDTO userInfoDTO = new EmployeeInfoDTO();
                userInfoDTO.setType(2);
                List<Map<String, Object>> userCustomFields = JsonUtils.toObj(remiDetailResDTO.getUserCustomFieldsStr(), new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
                });
                String userEmployeeNo = StringUtils.isBlank(remiDetailResDTO.getUserEmployeeNumber()) ? ucUserEmployeeNumber:remiDetailResDTO.getUserEmployeeNumber();
                String userEmployeePhone = StringUtils.isBlank(remiDetailResDTO.getUserPhone())?ucUserPhoneNumber:remiDetailResDTO.getUserPhone();
                String userDepartmentId = StringUtils.isBlank(remiDetailResDTO.getUserDepartmentId())?ucUserOrgUnitId:remiDetailResDTO.getUserDepartmentId();
                String userDepartmentName = StringUtils.isBlank(remiDetailResDTO.getUserDepartmentName())?ucUserOrgUnitName:remiDetailResDTO.getUserDepartmentName();
                String userThirdEmployeeId = StringUtils.isBlank(remiDetailResDTO.getUserThirdEmployeeId())?ucUserThirdEmployeeId:remiDetailResDTO.getUserThirdEmployeeId();
                String userThirdDeptId = StringUtils.isBlank(remiDetailResDTO.getThirdUserDepartmentId())?ucUserThirdOrgId:remiDetailResDTO.getThirdUserDepartmentId();
                List<Map<String, Object>> userExtInfo = ObjectUtils.isEmpty(userCustomFields)?ucUserExtInfoList:userCustomFields;
                userInfoDTO.setEmployeeNo(userEmployeeNo);
                userInfoDTO.setThirdEmployeeId(userThirdEmployeeId);
                userInfoDTO.setEmployeePhone(userEmployeePhone);
                userInfoDTO.setEmployeeDepartmentId(userDepartmentId);
                userInfoDTO.setEmployeeDepartmentName(userDepartmentName);
                userInfoDTO.setThirdDepartmentId(userThirdDeptId);

                List<KVEntity> userCustomField = new ArrayList<KVEntity>();
                if (!ObjectUtils.isEmpty(userExtInfo)) {
                    Map<String, Object> employeeExtInfo = userExtInfo.get(0);
                    for (Map.Entry<String, Object> entry : employeeExtInfo.entrySet()) {
                        KVEntity kvEntity = new KVEntity();
                        kvEntity.setKey(entry.getKey());
                        kvEntity.setValue(entry.getValue());
                        userCustomField.add(kvEntity);
                    }
                    if (!ObjectUtils.isEmpty(userCustomField)) {
                        userInfoDTO.setCustomFields(userCustomField);
                    }
                }

                employeeInfoList.add(userInfoDTO);
                remiDetailResDTO.setEmployeeNumber(null);
                remiDetailResDTO.setThirdEmployeeId(null);
                remiDetailResDTO.setEmployeeInfo(employeeInfoList);
                remiDetailResDTO.setCompanyId(companyId);
                // 替换报销单号
                if (!ObjectUtils.isEmpty(remiDetailMap.get("meaning_no"))) {
                    remiDetailResDTO.setReimbId(StringUtils.obj2str(remiDetailMap.get("meaning_no")));
                }
                BeanUtils.copyProperties(remiDetailResDTO, remiDetailDTO);
                remiDetailResList.add(remiDetailDTO);
            }
        }
        return remiDetailResList;
    }

    private void checkDateFormat(RemiDetailReqDTO req) {
        List<String> dateList = new ArrayList();
        dateList.add(req.getStartTime());
        dateList.add(req.getEndTime());
        dateList.add(req.getPaymentStartTime());
        dateList.add(req.getPaymentEndTime());
        dateList.add(req.getFinalApproveStartTime());
        dateList.add(req.getFinalApproveEndTime());
        for(String date:dateList){
            if (!StringUtils.isBlank(date)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    sdf.parse(date);
                } catch (ParseException e) {
                    log.info("日期格式异常，date:{}", date);
                    throw new OpenApiArgumentException("日期错误，请检查");
                }
            }
        }
    }
}
