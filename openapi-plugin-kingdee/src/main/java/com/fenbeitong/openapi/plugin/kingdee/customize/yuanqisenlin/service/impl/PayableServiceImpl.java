package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillPersonalApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillDataListDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillDataUserDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillPersonalConsumeDTO;
import com.fenbeitong.fenbei.settlement.external.api.query.BillDataListQuery;
import com.fenbeitong.fenbei.settlement.external.api.query.BillPersonalConsumeQuery;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant.Constant;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant.PayableTypeEnum;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto.KingdeePayableCommitDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto.KingdeePayableDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.IKingdeeCommonService;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.PayableService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenThirdKingdeeConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.bill.constants.OrderCategory;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackStatus;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ctl
 * @date 2021/7/2
 */
@ServiceAspect
@Service
@Slf4j
public class PayableServiceImpl implements PayableService {

    @DubboReference(check = false)
    private IBillOpenApi stereoBillService;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService iBaseEmployeeExtService;

    @Autowired
    private OpenThirdKingdeeConfigDao openThirdKingdeeConfigDao;

    @Autowired
    private KingdeeService kingdeeService;

    @Autowired
    private KingdeeConfig kingdeeConfig;

    @DubboReference(check = false)
    private IBillPersonalApi stereoBillPersonalService;
    @Autowired
    private OpenEmployeeServiceImpl employeeService;

    @Autowired
    private IKingdeeCommonService kingdeeCommonService;

    @Override
    public void convertPayable(String companyId, String billNo, String kingDeeCompanyFieldName, String kingDeeDeptFieldName) {
        /*
         * 1.从stereo查账单
         * 2.转换账单格式 生成应付单
         * 3.存入callback_record表中 status=-1 type=4
         */

        // 1.获取商务消费所有账单
        List<BillDataListDTO> allBillList = getAllBill(companyId, billNo);
        //2.获取个人消费所有账单
        List<BillDataListDTO> allPersonalBill = getAllPersonalBill(billNo, companyId);

        // 3.转换商务消费格式
        List<KingdeePayableDTO> payableList = convert(allBillList, companyId, PayableTypeEnum.BUSINESS_TYPE.getSpendingType(), kingDeeCompanyFieldName, kingDeeDeptFieldName);//1:商务
        // 4.转换个人消费格式
        List<KingdeePayableDTO> personPayableList = convert(allPersonalBill, companyId, PayableTypeEnum.PERSONAL_TYPE.getSpendingType(), kingDeeCompanyFieldName, kingDeeDeptFieldName);//2：个人消费

        // 3.存入表中
        savePayable(payableList, companyId);
        savePayable(personPayableList, companyId);
    }

    @Override
    public Object pushPayable(KingdeePayableDTO data, String companyId) {
        /*
         * 1.登陆金蝶获取cookie
         * 2.保存应付单
         * 3.提交应付单
         */

        // 查询配置
        OpenThirdKingdeeConfig openThirdKingdeeConfig = kingdeeCommonService.getOpenThirdKingdeeConfig(companyId);

        // 登陆并获取cookie
        String cookie = kingdeeCommonService.loginAndGetCookie(openThirdKingdeeConfig);
        if (StringUtils.isBlank(cookie)) {
            return OpenapiResponseUtils.error(500, "登陆失败");
        }

        // 保存
        return saveAndSubmit(data, openThirdKingdeeConfig, cookie);
    }

    /**
     * 保存并提交
     *
     * @param data
     * @param openThirdKingdeeConfig
     * @param cookie
     */
    private Object saveAndSubmit(KingdeePayableDTO data, OpenThirdKingdeeConfig openThirdKingdeeConfig, String cookie) {
        ResultVo save = kingdeeService.save(openThirdKingdeeConfig.getUrl() + kingdeeConfig.getSave(), cookie, JsonUtils.toJson(data));
        if (save.getCode() == 0) {
            Map result = (Map) save.getData();
            JSONArray successEntitys = (JSONArray) result.get("SuccessEntitys");
            JSONObject jsonObject = (JSONObject) successEntitys.get(0);
            String number = (String) jsonObject.get("Number");
            log.info("create payable save success, number:{} ", number);

            // 提交
            KingdeePayableCommitDTO kingdeePayableCommitDTO = new KingdeePayableCommitDTO();
            kingdeePayableCommitDTO.setFormId(Constant.PAYABLE_FORM_ID);
            KingdeePayableCommitDTO.Resource resource = new KingdeePayableCommitDTO.Resource();
            resource.setNumbers(new ArrayList<String>() {{
                add(number);
            }});
            kingdeePayableCommitDTO.setData(resource);
            ResultVo submit = kingdeeService.submit(openThirdKingdeeConfig.getUrl() + kingdeeConfig.getSubmit(), cookie, JsonUtils.toJson(kingdeePayableCommitDTO));
            if (submit.getCode() == 0) {
                log.info("create payable submit success, number:{} ", number);
                return OpenapiResponseUtils.success(new HashMap<>());
            } else {
                return OpenapiResponseUtils.error(500, "应付单提交失败");
            }
        } else {
            return OpenapiResponseUtils.error(500, "应付单保存失败");
        }
    }


    /**
     * 存入表中
     *
     * @param payableList
     */
    private void savePayable(List<KingdeePayableDTO> payableList, String companyId) {
        CompanyNewDto companyNewDto = ucCompanyService.getCompanyService().queryCompanyNewByCompanyId(companyId);
        for (KingdeePayableDTO kingdeePayableDTO : payableList) {
            ThirdCallbackRecord record = ThirdCallbackRecord.builder()
                .callbackData(JsonUtils.toJson(kingdeePayableDTO))
                .callbackStatus(CallbackStatus.NEED_CALLBACK.getStatus())
                .callbackType(CallbackType.PAYABLE_PUSH.getType())
                .companyId(companyId)
                .companyName(companyNewDto.getCompanyName())
                .build();
            recordDao.saveSelective(record);
        }
    }

    /**
     * 账单生成应付单
     *
     * @param allBillList
     * @return
     */
    private List<KingdeePayableDTO> convert(List<BillDataListDTO> allBillList, String companyId, Integer type, String kingDeeCompanyFieldName, String kingDeeDeptFieldName) {
        // 获取项目费用编码
        OpenThirdKingdeeConfig openThirdKingdeeConfig = kingdeeCommonService.getOpenThirdKingdeeConfig(companyId);
        Map<String, Object> expandMap = JsonUtils.toObj(openThirdKingdeeConfig.getExpandInfo(), Map.class);
        if (expandMap == null) {
            throw new OpenApiArgumentException("没有配置项目费用编码");
        }
        Map<String, String> codeProjectMapping = (Map<String, String>) expandMap.get(Constant.CODE_MAPPING_KEY);

        /*
         * 合同主体 决定生成几个应付单
         *    ｜｜
         *    ｜｜--- 火车票代打 生成一条数据 全部计入财务共享中心 事由为代理费
         *    ｜｜--- 其他订单 按  金蝶部门 + 事由  决定应付单中的明细有多少条    税额相关计算 是按场景分开计算 然后汇总在一条里
         *
         * 都在扩展字段里
         * contract_company_name     合同主体公司名称
         * contract_company_id       合同主体公司id
         * kingdee_department_id     金蝶部门编码
         */
        List<KingdeePayableDTO> targetList = new ArrayList<>();
        //公司主体过滤,打印错误信息
        List contractCompanyEmptyList= allBillList.stream().filter(k -> ObjectUtils.isEmpty(getBillGroupKeyValue(k, kingDeeCompanyFieldName, "contract_company_id2", "contract_company_id1"))).map(p->p.getEmployeeId()).collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(contractCompanyEmptyList)){
            log.info("部分人员合同主体为空,人员id：{}",JsonUtils.toJson(contractCompanyEmptyList));
            throw new OpenApiArgumentException("存在人员合同主体为空！");
        }
        // 按公司主体拆分 map的size就是应付单的数量 map的key是公司合同主体公司id value是对应的账单列表
        Map<Object, List<BillDataListDTO>> companyMap = allBillList.stream()
            .collect(Collectors.groupingBy(k -> getBillGroupKeyValue(k, kingDeeCompanyFieldName, "contract_company_id2", "contract_company_id1")));

        companyMap.forEach((contractCompanyId, companyGroupList) -> {
            // KingdeePayableDTO 是应付单对象 这个对应的个数代表应付单个数
            KingdeePayableDTO kingdeePayableDTO = new KingdeePayableDTO();
            KingdeePayableDTO.Resource data = new KingdeePayableDTO.Resource();
            KingdeePayableDTO.Resource.ModelDTO modelDTO = new KingdeePayableDTO.Resource.ModelDTO();
            // FEntityDTO 是应付单中的明细 这个list的size代表应付单的明细有多少条
            List<KingdeePayableDTO.Resource.ModelDTO.FEntityDTO> fEntityDTOList = new ArrayList<>();

            // 计算火车票代打的 不用按部门区分 单独计算
            calculateTrainProxy(codeProjectMapping, companyGroupList, fEntityDTOList);

            // 计算火车票代打除外的 按部门 事由区分
            calculateNotTrainProxy(codeProjectMapping, companyGroupList, fEntityDTOList, type, kingDeeDeptFieldName);

            // 实体主键
            modelDTO.setId(0);
            // 单据类型 必填 QTYFD01_SYS 其他应付单
            KingdeePayableDTO.Resource.ModelDTO.FBillTypeIDDTO fBillTypeIDDTO = new KingdeePayableDTO.Resource.ModelDTO.FBillTypeIDDTO();
            fBillTypeIDDTO.setNumber(Constant.OTHER_PAYABLE);
            modelDTO.setBillTypeID(fBillTypeIDDTO);
            // 业务日期 账单最后一天 格式:yyyy-MM-dd HH:mm:ss
            modelDTO.setDate(DateUtils.toStr(companyGroupList.get(0).getBillEndDate(), DateUtils.FORMAT_DATE_TIME_PATTERN));
            // 到期日 按业务日期算
            modelDTO.setEnddateH(modelDTO.getDate());
            // 是否期初单据
            modelDTO.setIsinit(false);
            // 往来单位类型 供应商 BD_Supplier
            modelDTO.setContactunittype(Constant.BD_SUPPLIER);
            // 往来单位 分贝通在元气森林金蝶的部门编码 生产:YQ0600949
            KingdeePayableDTO.Resource.ModelDTO.FCONTACTUNITDTO fcontactunitdto = new KingdeePayableDTO.Resource.ModelDTO.FCONTACTUNITDTO();
            fcontactunitdto.setNumber(expandMap.get(Constant.SUPPLIER_KEY).toString());
            modelDTO.setContactunit(fcontactunitdto);
            // 币别 必填 PRE001 人民币
            KingdeePayableDTO.Resource.ModelDTO.FCURRENCYIDDTO fcurrencyiddto = new KingdeePayableDTO.Resource.ModelDTO.FCURRENCYIDDTO();
            fcurrencyiddto.setNumber(Constant.RMB);
            modelDTO.setCurrencyid(fcurrencyiddto);
            // 总金额 按单据体list中总金额的汇总计算
            modelDTO.setTotalamountforH(fEntityDTOList.stream().map(KingdeePayableDTO.Resource.ModelDTO.FEntityDTO::getTotalamountfor).reduce(BigDecimal.ZERO, BigDecimal::add));
            // 未借款金额 按总金额算
            modelDTO.setNotsettleamountfor(modelDTO.getTotalamountforH());
            // 结算组织
            KingdeePayableDTO.Resource.ModelDTO.FSETTLEORGIDDTO fsettleorgiddto = new KingdeePayableDTO.Resource.ModelDTO.FSETTLEORGIDDTO();
            fsettleorgiddto.setNumber(String.valueOf(contractCompanyId));
            modelDTO.setSettleorgid(fsettleorgiddto);
            // 采购组织
            KingdeePayableDTO.Resource.ModelDTO.FPURCHASEORGIDDTO fpurchaseorgiddto = new KingdeePayableDTO.Resource.ModelDTO.FPURCHASEORGIDDTO();
            fpurchaseorgiddto.setNumber(String.valueOf(contractCompanyId));
            modelDTO.setPurchaseorgid(fpurchaseorgiddto);
            // 付款组织
            KingdeePayableDTO.Resource.ModelDTO.FPAYORGIDDTO fpayorgiddto = new KingdeePayableDTO.Resource.ModelDTO.FPAYORGIDDTO();
            fpayorgiddto.setNumber(String.valueOf(contractCompanyId));
            modelDTO.setPayorgid(fpayorgiddto);
            // 本位币 人民币 PRE001
            KingdeePayableDTO.Resource.ModelDTO.FMAINBOOKSTDCURRIDDTO fmainbookstdcurriddto = new KingdeePayableDTO.Resource.ModelDTO.FMAINBOOKSTDCURRIDDTO();
            fmainbookstdcurriddto.setNumber(Constant.RMB);
            modelDTO.setMainbookstdcurrid(fmainbookstdcurriddto);
            // 汇率类型 HLTX01_SYS 固定汇率
            KingdeePayableDTO.Resource.ModelDTO.FEXCHANGETYPEDTO fexchangetypedto = new KingdeePayableDTO.Resource.ModelDTO.FEXCHANGETYPEDTO();
            fexchangetypedto.setNumber(Constant.FIXED_TAX_RATE);
            modelDTO.setExchangetype(fexchangetypedto);
            // 汇率
            modelDTO.setExchangeRate(BigDecimal.valueOf(1.0));
            // 到期日计算日期
            modelDTO.setAccnttimejudgetime(modelDTO.getEnddateH());
            // 作废状态 必填 A
            modelDTO.setCancelStatus("A");
            // 业务类型 T
            modelDTO.setBusinesstype("T");
            // 适用范围 A
            modelDTO.setScopeofapplication("A");
            // 单据体
            modelDTO.setEntity(fEntityDTOList);
            // 数据包
            data.setModel(modelDTO);
            // 表单id
            kingdeePayableDTO.setFormId(Constant.PAYABLE_FORM_ID);
            kingdeePayableDTO.setData(data);
            targetList.add(kingdeePayableDTO);
        });
        return targetList;
    }

    private Object getBillGroupKeyValue(BillDataListDTO bill, String kingDeeFieldName, String userFieldName, String employeeFieldName) {
        List<BillDataUserDTO> consumerBeanList = bill.getConsumerBeanList();
        BillDataUserDTO userPerson = ObjectUtils.isEmpty(consumerBeanList) ? null : consumerBeanList.get(0);
        BillDataUserDTO personBean = bill.getPersonBean();
        //企业web 页面自定义字段值
        String customValue = getCustomValue(userPerson, personBean, kingDeeFieldName);
        if (!ObjectUtils.isEmpty(customValue)) {
            return customValue;
        }
        Map thirdInfoMap = JsonUtils.toObj(bill.getThirdInfo(), Map.class);
        //使用人
        String userValue = ObjectUtils.isEmpty(thirdInfoMap) ? null : (String) thirdInfoMap.get(userFieldName);
        //下单人
        String personValue = ObjectUtils.isEmpty(thirdInfoMap) ? null : (String) thirdInfoMap.get(employeeFieldName);
        return ObjectUtils.isEmpty(userValue) ? personValue : userValue;
    }

    private String getCustomValue(BillDataUserDTO userPerson, BillDataUserDTO personBean, String kingDeeFieldName) {
        String customValue = null;
        //使用人自定义字段
        String userCustomJson = userPerson == null ? null : userPerson.getCustomFiledJson();
        if (!ObjectUtils.isEmpty(userCustomJson)) {
            Map userCustomMap = JsonUtils.toObj(userCustomJson, Map.class);
            List<Map<String, Object>> filedValueList = userCustomMap == null ? null : (List<Map<String, Object>>) userCustomMap.get(userPerson.getId());
            Map<String, Object> filedKeyValue = ObjectUtils.isEmpty(filedValueList) ? null : filedValueList.stream().filter(fv -> kingDeeFieldName.equals(fv.get("key"))).findFirst().orElse(null);
            customValue = ObjectUtils.isEmpty(filedKeyValue) ? null : (String) filedKeyValue.get("value");
        }
        //下单人自定义字段
        String personCustomJson = personBean == null ? null : personBean.getCustomFiledJson();
        if (ObjectUtils.isEmpty(customValue) && !ObjectUtils.isEmpty(personCustomJson)) {
            Map personCustomMap = JsonUtils.toObj(personCustomJson, Map.class);
            List<Map<String, Object>> filedValueList = personCustomMap == null ? null : (List<Map<String, Object>>) personCustomMap.get(personBean.getId());
            Map<String, Object> filedKeyValue = ObjectUtils.isEmpty(filedValueList) ? null : filedValueList.stream().filter(fv -> kingDeeFieldName.equals(fv.get("key"))).findFirst().orElse(null);
            customValue = ObjectUtils.isEmpty(filedKeyValue) ? null : (String) filedKeyValue.get("value");
        }
        return customValue;
    }

    /**
     * 计算火车票代打除外的
     *
     * @param codeProjectMapping
     * @param companyGroupList
     * @param fEntityDTOList
     */
    private void calculateNotTrainProxy(Map<String, String> codeProjectMapping, List<BillDataListDTO> companyGroupList, List<KingdeePayableDTO.Resource.ModelDTO.FEntityDTO> fEntityDTOList, Integer type, String kingDeeDeptFieldName) {
        List<BillDataListDTO> notTrainProxyList = companyGroupList.stream().filter(
            e -> !e.getOrderCategory().equals(OrderCategory.ALTMAN.getValue()) ||
                (!"火车票打印服务".equals(e.getOrderCategoryType()) && !"代打火车票".equals(e.getTripType()))
        ).collect(Collectors.toList());

        List kindDeptEmptyList= notTrainProxyList.stream().filter(k -> ObjectUtils.isEmpty(getBillGroupKeyValue(k, kingDeeDeptFieldName, "kingdee_department_id2", "kingdee_department_id1"))).map(p->p.getEmployeeId()).collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(kindDeptEmptyList)){
            log.info("部分人员金蝶部门编码为空,人员id：{}",JsonUtils.toJson(kindDeptEmptyList));
            throw new OpenApiArgumentException("存在人员金蝶部门编码为空！");
        }
        // 根据金蝶部门 再细分一次
        Map<Object, List<BillDataListDTO>> departmentGroup = notTrainProxyList.stream()
            .collect(Collectors.groupingBy(k -> getBillGroupKeyValue(k, kingDeeDeptFieldName, "kingdee_department_id2", "kingdee_department_id1")));
        departmentGroup.forEach((departmentId, departmentGroupList) -> {

            List<String> reasonEmptyList = departmentGroupList.stream().filter(e -> ObjectUtils.isEmpty(parseReason(e.getReason()))).map(p -> p.getEmployeeId()).collect(Collectors.toList());
            if(!ObjectUtils.isEmpty(reasonEmptyList)){
                log.info("部分人员订单事由为空,人员id：{}",JsonUtils.toJson(reasonEmptyList));
                throw new OpenApiArgumentException("存在订单事由为空！");
            }
            // 根据事由 再分一次 事由可能会是"xxxx-xxxx"的结构，取前面的
            Map<String, List<BillDataListDTO>> reasonGroup = departmentGroupList.stream()
                .collect(Collectors.groupingBy(e -> parseReason(e.getReason())));

            reasonGroup.forEach((reason, reasonGroupList) -> {
                // 总金额 是0的直接跳过
                BigDecimal totalAmount = reasonGroupList.stream().map(BillDataListDTO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
                    KingdeePayableDTO.Resource.ModelDTO.FEntityDTO fEntityDTO = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO();
                    // 费用项目编码 通过reason 反查code
                    KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTIDDTO fcostiddto = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTIDDTO();
                    fcostiddto.setNumber(codeProjectMapping.get(reason));
                    fEntityDTO.setCostid(fcostiddto);
                    // 费用承担部门
                    KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTDEPARTMENTIDDTO fcostdepartmentid = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTDEPARTMENTIDDTO();
                    fcostdepartmentid.setNumber(String.valueOf(departmentId));
                    fEntityDTO.setCostdepartmentid(fcostdepartmentid);
                    // 计算税额相关
                    Map<String, BigDecimal> taxMap = calculateTax(reasonGroupList, type);
                    // 不含税金额 通过公式 按场景计算汇总
                    fEntityDTO.setNotaxamountfor(taxMap.get(Constant.EXCLUDE_TAX_AMOUNT));
                    // 税额 通过公式 按场景计算汇总
                    fEntityDTO.setTaxamountfor(taxMap.get(Constant.TAX_AMOUNT));
                    // 总金额
                    fEntityDTO.setTotalamountfor(totalAmount);
                    // 未借款金额=总金额
                    fEntityDTO.setNotsettleamountforD(fEntityDTO.getTotalamountfor());
                    // 不含税本位币金额=不含税金额
                    fEntityDTO.setNotaxamountD(fEntityDTO.getNotaxamountfor());
                    // 税额本位币=税额
                    fEntityDTO.setTaxamountD(fEntityDTO.getTaxamountfor());
                    // 已生成发票
                    fEntityDTO.setCreateinvoice(false);
                    // 税率名称
                    KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FTaxRateNameDTO taxRateName = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FTaxRateNameDTO();
                    taxRateName.setNumber(Constant.TAX_RATE_NAME_SL06_SYS);
                    fEntityDTO.setTaxRateName(taxRateName);
                    // 研发项目 暂时不做
                    //fEntityDTO.setFPfgcAssistant();
                    fEntityDTOList.add(fEntityDTO);
                }
            });
        });
    }

    /**
     * 计算火车票代打服务相关费用
     *
     * @param codeProjectMapping
     * @param companyGroupList
     * @param fEntityDTOList
     */
    private void calculateTrainProxy(Map<String, String> codeProjectMapping, List<BillDataListDTO> companyGroupList, List<KingdeePayableDTO.Resource.ModelDTO.FEntityDTO> fEntityDTOList) {
        List<BillDataListDTO> trainProxyList = companyGroupList.stream().filter(
            e -> e.getOrderCategory().equals(OrderCategory.ALTMAN.getValue()) &&
                ("火车票打印服务".equals(e.getOrderCategoryType()) || "代打火车票".equals(e.getTripType()))
        ).collect(Collectors.toList());

        // 不为0的再计算
        BigDecimal trainProxyTotalAmount = trainProxyList.stream().map(BillDataListDTO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (trainProxyTotalAmount.compareTo(BigDecimal.ZERO) != 0) {
            KingdeePayableDTO.Resource.ModelDTO.FEntityDTO fEntityDTO = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO();
            // 默认事由为“代理费”，金蝶费用编码为“C028”
            KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTIDDTO fcostiddto = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTIDDTO();
            fcostiddto.setNumber(codeProjectMapping.get("代理费"));
            fEntityDTO.setCostid(fcostiddto);
            // 费用承担部门
            KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTDEPARTMENTIDDTO fcostdepartmentid = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FCOSTDEPARTMENTIDDTO();
            fcostdepartmentid.setNumber(Constant.FINANCIAL_SHARING_CENTER);
            fEntityDTO.setCostdepartmentid(fcostdepartmentid);
            // 计算税额相关 税额=ROUND((企业支付金额/1.06*0.06),2)
            BigDecimal trainProxyTotalExcludeTax = BigDecimal.valueOf(0);
            BigDecimal trainProxyTotalTax = BigDecimal.valueOf(0);
            for (BillDataListDTO billDataListDTO : trainProxyList) {
                BigDecimal tax = billDataListDTO.getTotalAmount().divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
                trainProxyTotalExcludeTax = trainProxyTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                trainProxyTotalTax = trainProxyTotalTax.add(tax);
            }
            // 不含税金额 通过公式 按场景计算汇总
            fEntityDTO.setNotaxamountfor(trainProxyTotalExcludeTax.setScale(2, BigDecimal.ROUND_HALF_UP));
            // 税额 通过公式 按场景计算汇总
            fEntityDTO.setTaxamountfor(trainProxyTotalTax.setScale(2, BigDecimal.ROUND_HALF_UP));
            // 总金额
            fEntityDTO.setTotalamountfor(trainProxyTotalAmount);
            // 未借款金额=总金额
            fEntityDTO.setNotsettleamountforD(fEntityDTO.getTotalamountfor());
            // 不含税本位币金额=不含税金额
            fEntityDTO.setNotaxamountD(fEntityDTO.getNotaxamountfor());
            // 税额本位币=税额
            fEntityDTO.setTaxamountD(fEntityDTO.getTaxamountfor());
            // 已生成发票
            fEntityDTO.setCreateinvoice(false);
            // 税率名称
            KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FTaxRateNameDTO taxRateName = new KingdeePayableDTO.Resource.ModelDTO.FEntityDTO.FTaxRateNameDTO();
            taxRateName.setNumber(Constant.TAX_RATE_NAME_SL06_SYS);
            fEntityDTO.setTaxRateName(taxRateName);
            // 研发项目 暂时不做
            //fEntityDTO.setFPfgcAssistant();
            fEntityDTOList.add(fEntityDTO);
        }
    }

    /**
     * 计算税额相关 不同场景不通策略 计算汇总
     *
     * @param reasonGroupList
     * @return
     */
    private Map<String, BigDecimal> calculateTax(List<BillDataListDTO> reasonGroupList, Integer type) {
        Map<String, BigDecimal> taxMap = new HashMap<>();
        // 国际机票不含税金额
        BigDecimal airIntlTotalExcludeTax = BigDecimal.valueOf(0);
        // 国内机票不含税金额
        BigDecimal airTotalExcludeTax = BigDecimal.valueOf(0);
        // 国内机票税额
        BigDecimal airTotalTax = BigDecimal.valueOf(0);
        // 酒店不含税金额
        BigDecimal hotelTotalExcludeTax = BigDecimal.valueOf(0);
        // 酒店税额
        BigDecimal hotelTotalTax = BigDecimal.valueOf(0);
        // 火车不含税金额
        BigDecimal trainTotalExcludeTax = BigDecimal.valueOf(0);
        // 火车税额
        BigDecimal trainTotalTax = BigDecimal.valueOf(0);
        // 用车不含税金额
        BigDecimal taxiTotalExcludeTax = BigDecimal.valueOf(0);
        // 用餐不含税金额
        BigDecimal dinnerTotalExcludeTax = BigDecimal.valueOf(0);
        // 外卖不含税金额
        BigDecimal takeOutTotalExcludeTax = BigDecimal.valueOf(0);
        // 采购不含税金额
        BigDecimal mallTotalExcludeTax = BigDecimal.valueOf(0);
        // 采购税额
        BigDecimal mallTotalTax = BigDecimal.valueOf(0);
        // 闪送不含税金额
        BigDecimal expressTotalExcludeTax = BigDecimal.valueOf(0);
        // 闪送税额
        BigDecimal expressTotalTax = BigDecimal.valueOf(0);
        // 快递不含税金额
        BigDecimal expressDeliveryTotalExcludeTax = BigDecimal.valueOf(0);
        // 快递税额
        BigDecimal expressDeliveryTotalTax = BigDecimal.valueOf(0);
        // 万能订单不含税金额
        BigDecimal altmanTotalExcludeTax = BigDecimal.valueOf(0);
        // 其他
        BigDecimal otherTotalExcludeTax = BigDecimal.valueOf(0);
        for (BillDataListDTO billDataListDTO : reasonGroupList) {
            if (type.equals(PayableTypeEnum.BUSINESS_TYPE.getSpendingType())) {
                if (billDataListDTO.getOrderCategory().equals(OrderCategory.AirIntl.getValue())) {
                    // 国际机票 税额=0 不含税金额=企业支付金额
                    airIntlTotalExcludeTax = airIntlTotalExcludeTax.add(billDataListDTO.getTotalAmount());
                } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.Air.getValue())) {
                    // 国内机票 税额=ROUND((票价+改签差价+燃油费/1.09*0.09),2) 仅公司员工计算
                    BigDecimal tax = BigDecimal.valueOf(0);
                    if (isCompanyEmployee(billDataListDTO.getEmployeeId())) {
                        // 改签单 税额=ROUND(升舱费/1.09*0.09),2)
                        if (isChangeOrder(billDataListDTO)) {
                            tax = billDataListDTO.getUpgrateFee().divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
                        } else {
                            BigDecimal salePrice = billDataListDTO.getSalePrice();
                            BigDecimal airportFee = billDataListDTO.getAirportFee();
                            BigDecimal endorseDiffFee = billDataListDTO.getEndorseDiffFee();
                            BigDecimal fuelFee = billDataListDTO.getFuelFee();
                            BigDecimal upgrateFee = billDataListDTO.getUpgrateFee();
                            tax = salePrice.add(endorseDiffFee).add(fuelFee).divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
                        }
                    }
                    airTotalExcludeTax = airTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                    airTotalTax = airTotalTax.add(tax);
                }
            }
            if (billDataListDTO.getOrderCategory().equals(OrderCategory.Hotel.getValue())) {
                // 酒店 税额=ROUND((企业支付金额/1.06*0.06),2)
                BigDecimal tax = billDataListDTO.getTotalAmount().divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
                hotelTotalExcludeTax = hotelTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                hotelTotalTax = hotelTotalTax.add(tax);
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.Train.getValue())) {
                // 火车 税额==ROUND(票价/1.09*0.09,2) 仅公司员工计算
                BigDecimal tax = BigDecimal.valueOf(0);
                if (isCompanyEmployee(billDataListDTO.getEmployeeId())) {
                    // 改签单 税额=ROUND(改签差价/1.09*0.09),2)
                    if (isChangeOrder(billDataListDTO)) {
                        tax = billDataListDTO.getEndorseDiffFee().divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
                    } else {
                        BigDecimal salePrice = billDataListDTO.getSalePrice();
                        tax = salePrice.divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
                    }
                }
                trainTotalExcludeTax = trainTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                trainTotalTax = trainTotalTax.add(tax);
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.Taxi.getValue())) {
                // 用车 税额=0 不含税金额=企业支付金额
                taxiTotalExcludeTax = taxiTotalExcludeTax.add(billDataListDTO.getTotalAmount());
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.Dinner.getValue())) {
                // 用餐 税额=0 不含税金额=企业支付金额
                dinnerTotalExcludeTax = dinnerTotalExcludeTax.add(billDataListDTO.getTotalAmount());
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.TAKEOUT.getValue())) {
                // 外卖 税额=0 不含税金额=企业支付金额
                takeOutTotalExcludeTax = takeOutTotalExcludeTax.add(billDataListDTO.getTotalAmount());
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.Mall.getValue())) {
                // 采购 税额=ROUND((企业支付金额/1.13*0.13),2)
                BigDecimal tax = billDataListDTO.getTotalAmount().divide(BigDecimal.valueOf(1.13), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.13));
                mallTotalExcludeTax = mallTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                mallTotalTax = mallTotalTax.add(tax);
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.EXPRESS.getValue())) {
                // 闪送 税额=ROUND((企业支付金额/1.06*0.06),2)
                BigDecimal tax = billDataListDTO.getTotalAmount().divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
                expressTotalExcludeTax = expressTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                expressTotalTax = expressTotalTax.add(tax);
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.EXPRESSDELIVERY.getValue())) {
                // 快递 税额=ROUND((企业支付金额/1.06*0.06),2)
                BigDecimal tax = billDataListDTO.getTotalAmount().divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
                expressDeliveryTotalExcludeTax = expressDeliveryTotalExcludeTax.add(billDataListDTO.getTotalAmount().subtract(tax));
                expressDeliveryTotalTax = expressDeliveryTotalTax.add(tax);
            } else if (billDataListDTO.getOrderCategory().equals(OrderCategory.ALTMAN.getValue())) {
                // 除火车票代打的万能订单 税额=0 不含税金额=企业支付金额
                altmanTotalExcludeTax = altmanTotalExcludeTax.add(billDataListDTO.getTotalAmount());
            } else {
                // 其他
                otherTotalExcludeTax = otherTotalExcludeTax.add(billDataListDTO.getTotalAmount());
            }
        }

        // 不含税金额汇总
        BigDecimal totalExcludeTax = airIntlTotalExcludeTax.add(airTotalExcludeTax).add(hotelTotalExcludeTax)
            .add(trainTotalExcludeTax).add(taxiTotalExcludeTax).add(dinnerTotalExcludeTax)
            .add(takeOutTotalExcludeTax).add(mallTotalExcludeTax).add(expressTotalExcludeTax)
            .add(expressDeliveryTotalExcludeTax).add(altmanTotalExcludeTax).add(otherTotalExcludeTax);

        // 税额汇总
        BigDecimal totalTax = airTotalTax.add(hotelTotalTax).add(trainTotalTax).add(mallTotalTax)
            .add(expressTotalTax).add(expressDeliveryTotalTax);

        // 保留2位小数
        BigDecimal targetTotalExcludeTax = totalExcludeTax.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal targetTotalTax = totalTax.setScale(2, BigDecimal.ROUND_HALF_UP);

        taxMap.put(Constant.EXCLUDE_TAX_AMOUNT, targetTotalExcludeTax);
        taxMap.put(Constant.TAX_AMOUNT, targetTotalTax);
        return taxMap;
    }

    /**
     * 是否改签单
     *
     * @param billDataListDTO
     * @return
     */
    private boolean isChangeOrder(BillDataListDTO billDataListDTO) {
        return "改签成功".equals(billDataListDTO.getTicketStatus()) && !billDataListDTO.getOrderId().equals(billDataListDTO.getSourceOrderId());
    }

    /**
     * 是否是企业员工
     *
     * @param employeeId
     * @return
     */
    private boolean isCompanyEmployee(String employeeId) {
        return iBaseEmployeeExtService.getEmployeeInfoByEmloyeeId(employeeId) != null;
    }

    /**
     * 获取所有账单
     *
     * @param companyId
     * @param billNo
     * @return
     */
    private List<BillDataListDTO> getAllBill(String companyId, String billNo) {
        List<BillDataListDTO> allBillList = new ArrayList<>();
        int pageIndex = 1;
        int pageSize = 50;
        BillDataListQuery queryOrderDetailReq = new BillDataListQuery();
        queryOrderDetailReq.setBillNo(billNo);
        queryOrderDetailReq.setCompanyId(companyId);
        queryOrderDetailReq.setPageIndex(pageIndex);
        queryOrderDetailReq.setPageSize(pageSize);
        BasePageDTO<BillDataListDTO> billDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
        List<BillDataListDTO> dataListList = billDetailRes == null ? new ArrayList<>() : billDetailRes.getDtoList();
        while (!ObjectUtils.isEmpty(dataListList)) {
            allBillList.addAll(dataListList);
            queryOrderDetailReq.setPageIndex(++pageIndex);
            billDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
            dataListList = billDetailRes == null ? new ArrayList<>() : billDetailRes.getDtoList();
        }
        return allBillList;
    }

    //个人账单格式转换
    private List<BillDataListDTO> convertDataFormat(String companyId, List<BillPersonalConsumeDTO> billPersonalList) {
        List<BillDataListDTO> billDataListDTOList = new ArrayList<>();
        for (BillPersonalConsumeDTO billPersonalDTO : billPersonalList) {
            BillDataListDTO billDataListDTO = new BillDataListDTO();
            String thirdInfo = billPersonalDTO.getVoucherInfoBean().getThirdExtJson();
            //对thirdInfo信息进行处理
            String contractCompanyId = StringUtil.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(thirdInfo, Map.class), "originalcontract_company_id"));
            String kingdeeDepartmentId = StringUtil.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(thirdInfo, Map.class), "originalkingdee_department_id"));
            String originalVoucherUserId = StringUtil.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(thirdInfo, Map.class), "originalVoucherUserId"));
            //将值赋给使用人
            Map thirdInfoMap = JsonUtils.toObj(thirdInfo, Map.class);
            thirdInfoMap.put("contract_company_id2", contractCompanyId);
            thirdInfoMap.put("kingdee_department_id2", kingdeeDepartmentId);
            String thirdIfno = JsonUtils.toJson(thirdInfoMap);
            //从分贝券中取出订单金额信息赋值给totalAmouont
            billDataListDTO.setThirdInfo(thirdIfno);//三方信息
            billDataListDTO.setOrderCategory(billPersonalDTO.getOrderInfoBean().getOrderCategory());
            billDataListDTO.setReason(billPersonalDTO.getVoucherInfoBean().getVoucherName());//reason为分贝券名称
            billDataListDTO.setTotalAmount(billPersonalDTO.getVoucherInfoBean().getVoucherAmount());
            billDataListDTO.setBillEndDate(billPersonalDTO.getSummaryInfoBean().getBillEndDate());
            billDataListDTO.setOrderId(billPersonalDTO.getOrderInfoBean().getOrderId());
            billDataListDTOList.add(billDataListDTO);
        }
        return billDataListDTOList;
    }

    /**
     * 获取个人消费账单
     *
     * @param billNo
     * @return
     */
    private List<BillDataListDTO> getAllPersonalBill(String billNo, String companyId) {
        List<BillPersonalConsumeDTO> allPersonalBillList = new ArrayList<>();
        int pageIndex = 1;
        int pageSize = 50;
        BillPersonalConsumeQuery billPersonalConsumeQuery = new BillPersonalConsumeQuery();
        billPersonalConsumeQuery.setBillNo(billNo);
        billPersonalConsumeQuery.setPageIndex(pageIndex);
        billPersonalConsumeQuery.setPageSize(pageSize);
        billPersonalConsumeQuery.setCompanyId(companyId);
        BasePageDTO<BillPersonalConsumeDTO> billPersonalRes = stereoBillPersonalService.queryPersonalConsumeFlowDetail(billPersonalConsumeQuery);
        List<BillPersonalConsumeDTO> billPersonalList = billPersonalRes == null ? new ArrayList<>() : billPersonalRes.getDtoList();
        while (!ObjectUtils.isEmpty(billPersonalList)) {
            allPersonalBillList.addAll(billPersonalList);
            billPersonalConsumeQuery.setPageIndex(++pageIndex);
            billPersonalRes = stereoBillPersonalService.queryPersonalConsumeFlowDetail(billPersonalConsumeQuery);
            billPersonalList = billPersonalRes == null ? new ArrayList<>() : billPersonalRes.getDtoList();
        }
        List<BillDataListDTO> billDataListDTOList = convertDataFormat(companyId, allPersonalBillList);
        return billDataListDTOList;
    }


    /**
     * 解析reason 兼容手填的错误数据
     * 正常只有"xxx-xxx"格式的，取"-"前面的
     *
     * @param reason
     * @return
     */
    private String parseReason(String reason) {
        String targetReason = "";
        if (reason.contains("-")) {
            String[] split = reason.split("-");
            if (!ObjectUtils.isEmpty(split)) {
                targetReason = split[0];
            }
        } else if (reason.contains(" ")) {
            String[] split = reason.split(" ");
            if (!ObjectUtils.isEmpty(split)) {
                targetReason = split[0];
            }
        } else {
            targetReason = reason;
        }
        return targetReason;
    }

}
