package com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.utils.DateUtils;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.constant.BillInvoiceType;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto.KingdeeSaveReimbursementDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto.OpenBillDetailQueryResDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto.OpenBillPageInfoResDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.service.AerfaReimburseService;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant.Constant;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant.PayableTypeEnum;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto.KingdeePayableCommitDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.IKingdeeCommonService;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.bill.constants.OrderCategory;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackStatus;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class AerfaReimburseServiceImpl implements AerfaReimburseService {

    @Autowired
    private IKingdeeCommonService kingdeeCommonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService iBaseEmployeeExtService;

    @Value("${host.tiger}")
    private String tigerHost;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private KingdeeService kingdeeService;

    @Autowired
    private KingdeeConfig kingdeeConfig;


    @Override
    public void convertReimb(String companyId, String billNo) {

        // 1.获取商务消费所有账单
        List<OpenBillDetailQueryResDTO> allBillList = getAllBill(companyId, billNo);

        // 3.转换商务消费格式
        List<KingdeeSaveReimbursementDTO> reimbDataList = convert(allBillList, companyId, PayableTypeEnum.BUSINESS_TYPE.getSpendingType());

        // 3.存入表中
        savePayable(reimbDataList, companyId);
    }

    public Object getKingdeeCookie(String companyId) {
        // 查询配置
        OpenThirdKingdeeConfig openThirdKingdeeConfig = kingdeeCommonService.getOpenThirdKingdeeConfig(companyId);
        // 登陆并获取cookie
        String cookie = kingdeeCommonService.loginAndGetCookie(openThirdKingdeeConfig);
        if (StringUtils.isBlank(cookie)) {
            return OpenapiResponseUtils.error(500, "登陆失败");
        }
        return cookie;
    }

    @Override
    public Object pushReimb(KingdeeSaveReimbursementDTO data, String companyId) {
        String cookie = StringUtils.obj2str(getKingdeeCookie(companyId));
        // 查询配置
        OpenThirdKingdeeConfig openThirdKingdeeConfig = kingdeeCommonService.getOpenThirdKingdeeConfig(companyId);
        ResultVo save = kingdeeService.save(openThirdKingdeeConfig.getUrl() + kingdeeConfig.getSave(), cookie, JsonUtils.toJson(data));
        if (save.getCode() == 0) {
            Map result = (Map) save.getData();
            JSONArray successEntitys = (JSONArray) result.get("SuccessEntitys");
            JSONObject jsonObject = (JSONObject) successEntitys.get(0);
            String number = (String) jsonObject.get("Number");
            log.info("create payable save success, number:{} ", number);
            // 提交
            KingdeePayableCommitDTO kingdeePayableCommitDTO = new KingdeePayableCommitDTO();
            kingdeePayableCommitDTO.setFormId(Constant.REIMBURSEMENT_FORM_ID);
            KingdeePayableCommitDTO.Resource resource = new KingdeePayableCommitDTO.Resource();
            resource.setNumbers(new ArrayList<String>() {{
                add(number);
            }});
            kingdeePayableCommitDTO.setData(resource);
            ResultVo submit = kingdeeService.submit(openThirdKingdeeConfig.getUrl() + kingdeeConfig.getSubmit(), cookie, JsonUtils.toJson(kingdeePayableCommitDTO));
            if (submit.getCode() == 0) {
                log.info("create reimbursement submit success, number:{} ", number);
                return OpenapiResponseUtils.success(new HashMap<>());
            } else {
                return OpenapiResponseUtils.error(500, "费用报销单提交失败");
            }
        } else {
            return OpenapiResponseUtils.error(500, "费用报销单保存失败");
        }
    }

    /**
     * 账单生成应付单
     *
     * @param allBillList
     * @return
     */
    private List<KingdeeSaveReimbursementDTO> convert(List<OpenBillDetailQueryResDTO> allBillList, String companyId, Integer type) {

        List contractCompanyEmptyList = allBillList.stream().filter(k -> ObjectUtils.isEmpty(k.getOrderCustomField1())).map(p -> p.getPayerCode()).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(contractCompanyEmptyList)) {
            log.info("部分人员申请组织为空,人员编码：{}", JsonUtils.toJson(contractCompanyEmptyList));
            throw new OpenApiArgumentException("存在人员申请组织为空！");
        }
        // 按申请组织拆分 map的size就是应付单的数量 map的key是公司申请组织 value是对应的账单列表
        Map<Object, List<OpenBillDetailQueryResDTO>> companyMap = allBillList.stream().collect(Collectors.groupingBy(OpenBillDetailQueryResDTO::getOrderCustomField1));

        List<KingdeeSaveReimbursementDTO> kingdeeSaveReimbursementDTOS = new ArrayList<>();
        companyMap.forEach((contractCompanyId, companyGroupList) -> {
            // 计算火车票代打除外的 火车票代打的不处理
            List<OpenBillDetailQueryResDTO> notTrainProxyList = companyGroupList.stream().filter(
                e -> e.getOrderCategory() != OrderCategory.ALTMAN.getValue() ||
                    (!"火车票打印服务".equals(e.getOrderCategoryType()) && !"代打火车票".equals(e.getTripName()))
            ).collect(Collectors.toList());
            buildGroupList(notTrainProxyList, type, companyId, kingdeeSaveReimbursementDTOS);
        });
        return kingdeeSaveReimbursementDTOS;
    }

    private List<KingdeeSaveReimbursementDTO> buildGroupList(List<OpenBillDetailQueryResDTO> notTrainProxyList, Integer type, String companyId, List<KingdeeSaveReimbursementDTO> reimbDataList) {
        // 获取项目费用编码
        OpenThirdKingdeeConfig openThirdKingdeeConfig = kingdeeCommonService.getOpenThirdKingdeeConfig(companyId);
        Map<String, Object> expandMap = JsonUtils.toObj(openThirdKingdeeConfig.getExpandInfo(), Map.class);
        if (expandMap == null) {
            throw new OpenApiArgumentException("没有配置项目费用编码");
        }
        Map<String, String> codeProjectMapping = (Map<String, String>) expandMap.get(Constant.CODE_MAPPING_KEY);
        //按照申请人再分一次
        List kindDeptEmptyList = notTrainProxyList.stream().filter(k -> ObjectUtils.isEmpty(k.getPayerCode())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(kindDeptEmptyList)) {
            log.info("部分人员金蝶编码为空,人员id：{}", JsonUtils.toJson(kindDeptEmptyList));
            throw new OpenApiArgumentException("存在人员金蝶编码为空！");
        }
        // 根据人员 再细分一次
        Map<Object, List<OpenBillDetailQueryResDTO>> personGroup = notTrainProxyList.stream()
            .collect(Collectors.groupingBy(k -> k.getPayerCode()));
        personGroup.forEach((personCode, personGroupList) -> {
            //往来单位，使用人的员工编码为空则设置为v99
            personGroupList.stream().map(t -> {
                //用餐场景的使用人即为申请人
                if (t.getOrderCategory() == OrderCategory.MEISHI.getValue() || t.getOrderCategory() == OrderCategory.Dinner.getValue()) {
                    t.setUserCode(t.getPayerCode());
                }
                return t;
            }).filter(e -> ObjectUtils.isEmpty(e.getUserCode())).forEach(p -> {
                p.setUserCode("v99");
                p.setUserName("虚拟(分贝通)");
            });
            // 根据往来单位 再分一次
            Map<String, List<OpenBillDetailQueryResDTO>> contactUnitGroup = personGroupList.stream()
                .collect(Collectors.groupingBy(e -> e.getUserCode()));
            //获取企业支付金额，正负用于判断正逆)向,企业支付金额为0的则不做处理
            contactUnitGroup.forEach((contactUnit, contactUnitGroupList) -> {
                //过滤掉企业支付金额为0的
                List<OpenBillDetailQueryResDTO> billOrderList = contactUnitGroupList.stream().filter(e -> {
                    return StringUtils.isNotBlank(e.getCompanyPrice()) && BigDecimal.ZERO.compareTo(new BigDecimal(e.getCompanyPrice())) != 0;
                }).map(e -> {
                    Integer paymentDirect = new BigDecimal(e.getCompanyPrice()).compareTo(BigDecimal.ZERO) > 0 ? 1 : 2;
                    e.setPaymentDirect(paymentDirect);
                    return e;
                }).collect(Collectors.toList());

                //根据正向逆向再分一次
                Map<Object, List<OpenBillDetailQueryResDTO>> paymentGroup = billOrderList.stream()
                    .collect(Collectors.groupingBy(k -> k.getPaymentDirect()));
                paymentGroup.forEach((paymentDirect, paymentDirectList) -> {
                    KingdeeSaveReimbursementDTO reimbData = new KingdeeSaveReimbursementDTO();
                    KingdeeSaveReimbursementDTO.Resource resource = new KingdeeSaveReimbursementDTO.Resource();
                    KingdeeSaveReimbursementDTO.Model model = new KingdeeSaveReimbursementDTO.Model();
                    //账单实体列表
                    List<KingdeeSaveReimbursementDTO.Model.FEntity> entityDTOList = new ArrayList<>();
                    String proposerId = null;
                    String billEntity = null;
                    String userCode = null;
                    String expenseDeptId = null;

                    BigDecimal companyGroupPayPrice = BigDecimal.ZERO;
                    for (int i = 0; i < paymentDirectList.size(); i++) {
                        //费用项目
                        KingdeeSaveReimbursementDTO.Model.FEntity fEntityDTO = new KingdeeSaveReimbursementDTO.Model.FEntity();
                        KingdeeSaveReimbursementDTO.FExpID expID = new KingdeeSaveReimbursementDTO.FExpID();
                        expID.setNumber(codeProjectMapping.get(paymentDirectList.get(i).getCostCategory()));
                        fEntityDTO.setExpId(expID);
                        //费用金额
                        BigDecimal companyPrice = new BigDecimal(paymentDirectList.get(i).getCompanyPrice());
                        companyPrice = companyPrice.compareTo(BigDecimal.ZERO) > 0 ? companyPrice : companyPrice.negate();
                        fEntityDTO.setTaxSubmitAmt(companyPrice);
                        // 计算税额相关
                        Map<String, Object> taxMap = calculateTax(paymentDirectList.get(i), type, companyId);
                        //税率
                        fEntityDTO.setTaxRate((BigDecimal) taxMap.get(Constant.TAX_RATE));
                        //税额
                        BigDecimal taxAmount = (BigDecimal) taxMap.get(Constant.TAX_AMOUNT);
                        taxAmount = taxAmount.compareTo(BigDecimal.ZERO) > 0 ? taxAmount : taxAmount.negate();
                        fEntityDTO.setTaxAmt(taxAmount);
                        //发票类型
                        fEntityDTO.setInvoiceType(StringUtils.obj2str(taxMap.get(Constant.INVOICE_TYPE)));
                        //费用承担部门
                        KingdeeSaveReimbursementDTO.FExpenseDeptEntryID expenseDeptEntryID = new KingdeeSaveReimbursementDTO.FExpenseDeptEntryID();
                        String archiveCode = StringUtils.isBlank(paymentDirectList.get(i).getArchiveCode1()) ? paymentDirectList.get(i).getArchiveCode2() : paymentDirectList.get(i).getArchiveCode1();
                        expenseDeptEntryID.setNumber(archiveCode);
                        fEntityDTO.setExpenseDeptEntryId(expenseDeptEntryID);
                        if (StringUtils.isBlank(expenseDeptId)) {
                            expenseDeptId = archiveCode;
                        }
                        //研发项目
                        KingdeeSaveReimbursementDTO.FCMYAssistant fcmyAssistant = new KingdeeSaveReimbursementDTO.FCMYAssistant();
                        fcmyAssistant.setNumber(paymentDirectList.get(i).getProjectCode());
                        fEntityDTO.setCmyAssistant(fcmyAssistant);
                        //申请报销金额
                        fEntityDTO.setExpSubmitAmount(new BigDecimal(paymentDirectList.get(i).getCompanyPrice()));
                        fEntityDTO.setRequestAmount(companyPrice);
                        //备注
                        fEntityDTO.setRemark(paymentDirectList.get(i).getOrderId());
                        entityDTOList.add(fEntityDTO);

                        //表头区域数据记录
                        //申请人
                        proposerId = paymentDirectList.get(i).getPayerCode();
                        //申请组织（开票主体）
                        String billEntityRep = paymentDirectList.get(i).getOrderCustomField1().replace("开票主体-", "").replace("默认取值", "");
                        billEntity = billEntityRep.substring(0, billEntityRep.indexOf("-"));
                        //往来单位
                        userCode = paymentDirectList.get(i).getUserCode();
                        if (paymentDirectList.get(i).getOrderCategory() == OrderCategory.MEISHI.getValue() || paymentDirectList.get(i).getOrderCategory() == OrderCategory.Dinner.getValue()) {
                            userCode = proposerId;
                        }
                        companyGroupPayPrice = companyGroupPayPrice.add(new BigDecimal(paymentDirectList.get(i).getCompanyPrice()));
                    }
                    model.setBillSource("分贝通");
                    //申请人
                    KingdeeSaveReimbursementDTO.FProposerID proposerID = new KingdeeSaveReimbursementDTO.FProposerID();
                    proposerID.setStafNumber(proposerId);
                    model.setProposeId(proposerID);
                    //申请组织
                    KingdeeSaveReimbursementDTO.FOrgID org = new KingdeeSaveReimbursementDTO.FOrgID();
                    org.setNumber(billEntity);
                    model.setOrgId(org);
                    //往来单位
                    KingdeeSaveReimbursementDTO.FCONTACTUNIT fcontactunit = new KingdeeSaveReimbursementDTO.FCONTACTUNIT();
                    fcontactunit.setNumber(userCode);
                    model.setContactUnit(fcontactunit);
                    //申请人所在金蝶部门编码
                    KingdeeSaveReimbursementDTO.FRequestDeptID requestDeptID = new KingdeeSaveReimbursementDTO.FRequestDeptID();
                    requestDeptID.setNumber(expenseDeptId);
                    model.setRequestDeptId(requestDeptID);
                    //费用承担部门
                    KingdeeSaveReimbursementDTO.FExpenseDeptID expenseDeptID = new KingdeeSaveReimbursementDTO.FExpenseDeptID();
                    expenseDeptID.setNumber(expenseDeptId);
                    model.setExpenseDepId(expenseDeptID);
                    //币别
                    KingdeeSaveReimbursementDTO.FCurrencyID currencyID = new KingdeeSaveReimbursementDTO.FCurrencyID();
                    currencyID.setNumber(Constant.RMB);
                    model.setCurrencyId(currencyID);
                    //申请日期
                    model.setApplyDate(DateUtils.getCurrentDate());
                    //费用承担组织
                    KingdeeSaveReimbursementDTO.FExpenseOrgId expenseOrgId = new KingdeeSaveReimbursementDTO.FExpenseOrgId();
                    expenseOrgId.setNumber(billEntity);
                    model.setExpenseOrgId(expenseOrgId);
                    //本位币：
                    //往来单位类型 默认取值为“员工”
                    model.setContactUnitType(Constant.BD_EMPINFO);
                    //单据类型
                    KingdeeSaveReimbursementDTO.FBillTypeID billTypeID = new KingdeeSaveReimbursementDTO.FBillTypeID();
                    billTypeID.setNumber(Constant.REIMBURSEMENT);
                    model.setBillTypeId(billTypeID);
                    //事由
                    model.setCausa("分贝通商务消费");
                    //费用类别
                    KingdeeSaveReimbursementDTO.FPAEZAssistant fpaezAssistant = new KingdeeSaveReimbursementDTO.FPAEZAssistant();
                    fpaezAssistant.setNumber("01");
                    model.setPaezAssistant(fpaezAssistant);
                    //默认FrefundBox为false,申请付款小于0时赋为true
                    model.setFrefundBox(false);
                    //申请付款
                    if (companyGroupPayPrice.compareTo(BigDecimal.ZERO) > 0) {
                        model.setPayBox(false);
                    } else {
                        model.setFrefundBox(true);
                    }
                    if (model.getFrefundBox()) {
                        //付款组织
                        KingdeeSaveReimbursementDTO.FPayOrgId payOrgId = new KingdeeSaveReimbursementDTO.FPayOrgId();
                        payOrgId.setNumber(billEntity);
                        model.setPayOrgId(payOrgId);
                        //结算方式
                        KingdeeSaveReimbursementDTO.FPaySettlleTypeID paySettlleTypeID = new KingdeeSaveReimbursementDTO.FPaySettlleTypeID();
                        paySettlleTypeID.setNumber(Constant.SETTLLE_TYPE_JSFS04_SYS);
                        model.setPaySettlleTypeId(paySettlleTypeID);
                    }
                    model.setEntity(entityDTOList);
                    resource.setModel(model);
                    reimbData.setData(resource);
                    reimbDataList.add(reimbData);
                });
            });
        });
        return reimbDataList;
    }

    /**
     * 计算税额相关 不同场景不通策略 计算汇总
     *
     * @param
     * @return
     */
    private Map<String, Object> calculateTax(OpenBillDetailQueryResDTO billDataListDTO, Integer type, String companyId) {
        Map<String, Object> taxMap = new HashMap<>();
        //单笔税额
        BigDecimal totalTaxAmt = BigDecimal.valueOf(0);
        BigDecimal taxRate = BigDecimal.valueOf(0);
        String invoiceType = new String();
        if (billDataListDTO.getOrderCategory() == OrderCategory.Air.getValue()) {
            // 国内机票 税额=ROUND((票价+改签差价+燃油费/1.09*0.09),2) 仅公司员工计算
            BigDecimal tax = BigDecimal.valueOf(0);
            // 改签单 税额=ROUND(升舱费/1.09*0.09),2)
            if (isChangeOrder(billDataListDTO)) {
                BigDecimal upgradeFee = StringUtils.isBlank(billDataListDTO.getUpgradeFee()) ? BigDecimal.ZERO : new BigDecimal(billDataListDTO.getUpgradeFee());
                // 机票改签单税额公式计算
                tax = upgradeFee.divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
            } else {
                BigDecimal salePrice = new BigDecimal(billDataListDTO.getGoodsPrice());
                BigDecimal fuelFee = StringUtils.isBlank(billDataListDTO.getFuelFee()) ? BigDecimal.ZERO : new BigDecimal(billDataListDTO.getFuelFee());
                BigDecimal rebookFee = StringUtils.isBlank(billDataListDTO.getRebookFee()) ? BigDecimal.ZERO : new BigDecimal(billDataListDTO.getRebookFee());
                tax = salePrice.add(fuelFee).add(rebookFee).divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
            }
            invoiceType = BillInvoiceType.getKingdeeInvoiceType(OrderCategory.Air.getValue());
            totalTaxAmt = totalTaxAmt.add(tax);
            taxRate = taxRate.add(BigDecimal.valueOf(9));
        } else if (billDataListDTO.getOrderCategory() == OrderCategory.Hotel.getValue()) {
            // 酒店 税额=ROUND((企业支付金额/1.06*0.06),2)
            BigDecimal tax = new BigDecimal(billDataListDTO.getCompanyPrice()).divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
            totalTaxAmt = totalTaxAmt.add(tax);
            taxRate = taxRate.add(BigDecimal.valueOf(6));
            invoiceType = BillInvoiceType.getKingdeeInvoiceType(OrderCategory.Hotel.getValue());
        } else if (billDataListDTO.getOrderCategory() == OrderCategory.Train.getValue()) {
            // 火车 税额==ROUND(票价/1.09*0.09,2) 仅公司员工计算
            BigDecimal tax = BigDecimal.valueOf(0);
            // 改签单 税额=ROUND(改签差价/1.09*0.09),2)
            BigDecimal rebookDiffPrice = StringUtils.isBlank(billDataListDTO.getRebookDiffPrice()) ? BigDecimal.ZERO : new BigDecimal(billDataListDTO.getRebookDiffPrice());
            if (isChangeOrder(billDataListDTO)) {
                tax = rebookDiffPrice.divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
            } else {
                BigDecimal salePrice = new BigDecimal(billDataListDTO.getGoodsPrice());
                tax = salePrice.divide(BigDecimal.valueOf(1.09), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.09));
            }
            totalTaxAmt = totalTaxAmt.add(tax);
            taxRate = taxRate.add(BigDecimal.valueOf(9));
            invoiceType = BillInvoiceType.getKingdeeInvoiceType(OrderCategory.Train.getValue());
        } else if (billDataListDTO.getOrderCategory() == OrderCategory.EXPRESS.getValue()) {
            // 闪送 税额=ROUND((企业支付金额/1.06*0.06),2)
            BigDecimal tax = new BigDecimal(billDataListDTO.getCompanyPrice()).divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
            totalTaxAmt = totalTaxAmt.add(tax);
            taxRate = taxRate.add(BigDecimal.valueOf(6));
            invoiceType = BillInvoiceType.getKingdeeInvoiceType(OrderCategory.EXPRESS.getValue());
        } else if (billDataListDTO.getOrderCategory() == OrderCategory.EXPRESSDELIVERY.getValue()) {
            // 快递 税额=ROUND((企业支付金额/1.06*0.06),2)
            BigDecimal tax = new BigDecimal(billDataListDTO.getCompanyPrice()).divide(BigDecimal.valueOf(1.06), 99, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.06));
            totalTaxAmt = totalTaxAmt.add(tax);
            taxRate = taxRate.add(BigDecimal.valueOf(6));
            invoiceType = BillInvoiceType.getKingdeeInvoiceType(OrderCategory.EXPRESSDELIVERY.getValue());
        } else {
            //其他不计税的场景对应普通发票
            invoiceType = BillInvoiceType.getKingdeeInvoiceType(OrderCategory.Taxi.getValue());
        }

        // 保留2位小数
        BigDecimal targetTotalTax = totalTaxAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
        taxMap.put(Constant.TAX_AMOUNT, targetTotalTax);
        taxMap.put(Constant.TAX_RATE, taxRate);
        taxMap.put(Constant.INVOICE_TYPE, invoiceType);
        return taxMap;
    }

    /**
     * 是否改签单
     *
     * @param billDataListDTO
     * @return
     */
    private boolean isChangeOrder(OpenBillDetailQueryResDTO billDataListDTO) {
        return "改签成功".equals(billDataListDTO.getOrderState()) && !billDataListDTO.getOrderId().equals(billDataListDTO.getRootOrderId());
    }

    /**
     * 根据手机号查询是否是企业员工
     *
     * @param companyId
     * @return
     */
    private boolean isCompanyEmployeeByPhone(String companyId, String phone) {
        return iBaseEmployeeExtService.queryEmployeeInfoByPhone(companyId, phone) != null;
    }

    /**
     * 获取所有账单
     *
     * @param companyId
     * @param
     * @return
     */
    private List<OpenBillDetailQueryResDTO> getAllBill(String companyId, String billCode) {
        int pageIndex = 1;
        int pageSize = 50;
        List<OpenBillDetailQueryResDTO> allBillList = new ArrayList<>();
        Map billReqData = new HashMap();
        billReqData.put("bill_code", billCode);
        billReqData.put("page_index", pageIndex);
        billReqData.put("page_size", pageSize);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("appId", companyId);

        String reqUrl = tigerHost + "/openapi/bill/business/v1/detail";
        log.info("阿尔法账单入参：url:{},head:{},params:{}", reqUrl, headers, JsonUtils.toJson(billReqData));
        String result = RestHttpUtils.postJson(reqUrl, headers, JsonUtils.toJson(billReqData));
        log.info("阿尔法账单返回数据:{}", JsonUtils.toJson(result));
        OpenApiResponseDTO respDTO = JsonUtils.toObj(result, OpenApiResponseDTO.class);
        OpenBillPageInfoResDTO billDataList = JsonUtils.toObj(JsonUtils.toJson(respDTO.getData()), OpenBillPageInfoResDTO.class);

        while (!ObjectUtils.isEmpty(billDataList) && billDataList.getTotalPages() >= billDataList.getPageIndex()) {
            List<OpenBillDetailQueryResDTO> openBillDetailQueryRes = JsonUtils.toObj(JsonUtils.toJson(billDataList.getDetails()), new TypeReference<List<OpenBillDetailQueryResDTO>>() {
            });
            allBillList.addAll(openBillDetailQueryRes);
            billReqData.put("page_index", ++pageIndex);
            String loopRes = RestHttpUtils.postJson(reqUrl, headers, JsonUtils.toJson(billReqData));
            OpenApiResponseDTO resp = JsonUtils.toObj(loopRes, OpenApiResponseDTO.class);
            billDataList = JsonUtils.toObj(JsonUtils.toJson(resp.getData()), OpenBillPageInfoResDTO.class);
        }
        return allBillList;
    }

    /**
     * 存入表中
     *
     * @param payableList
     */
    private void savePayable(List<KingdeeSaveReimbursementDTO> payableList, String companyId) {
        CompanyNewDto companyNewDto = ucCompanyService.getCompanyService().queryCompanyNewByCompanyId(companyId);
        for (KingdeeSaveReimbursementDTO kingdeePayableDTO : payableList) {
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
}
