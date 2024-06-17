package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.*;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant.Constant;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto.KingdeeExpReimbursementDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.IKingdeeCommonService;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.ReimburseBillService;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ServiceAspect
@Service
@Slf4j
public class ReimburseBillServiceImpl implements ReimburseBillService {

    @Autowired
    private KingdeeService kingdeeService;

    @Autowired
    private IKingdeeCommonService kingdeeCommonService;

    @Autowired
    private KingdeeConfig kingdeeConfig;

    @DubboReference(check=false)
    private ICommonService commonService;

    @Override
    public Object pushReimburseBill(RemiDetailResDTO data) {
        String companyId = data.getCompanyId();
        OpenThirdKingdeeConfig openThirdKingdeeConfig = kingdeeCommonService.getOpenThirdKingdeeConfig(companyId);
        //转换金蝶dto
        KingdeeExpReimbursementDTO kingdeeExpReimbursementDTO = convert(openThirdKingdeeConfig, data, companyId);
        //登录
        String cookie = kingdeeCommonService.loginAndGetCookie(openThirdKingdeeConfig);
        if (StringUtils.isBlank(cookie)) {
            return OpenapiResponseUtils.error(500, "登陆失败");
        }
        //保存提交审核
        ResultVo save = kingdeeService.save(openThirdKingdeeConfig.getUrl() + kingdeeConfig.getSave(), cookie, JsonUtils.toJson(kingdeeExpReimbursementDTO));
        if (save.getCode() == 0) {
            return OpenapiResponseUtils.success(new HashMap<>());
        } else {
            return OpenapiResponseUtils.error(500, JsonUtils.toJson(save.getData()));
        }
    }

    private KingdeeExpReimbursementDTO convert(OpenThirdKingdeeConfig openThirdKingdeeConfig, RemiDetailResDTO data, String companyId) {
        List<EmployeeInfoDTO> employeeInfoList = data.getEmployeeInfo();
        if (ObjectUtils.isEmpty(employeeInfoList)) {
            log.info("员工信息不存在");
            throw new OpenApiArgumentException("员工信息不存在");
        }
        //获取报销人信息
        EmployeeInfoDTO employee = null;
        for (EmployeeInfoDTO employeeInfo : employeeInfoList) {
            Integer type = employeeInfo.getType();
            if (type != null && type.equals(2)) {
                employee = employeeInfo;
            }
        }
        if (ObjectUtils.isEmpty(employee)) {
            log.info("员工不存在");
            throw new OpenApiArgumentException("员工不存在");
        }
        List<KVEntity> customFields = employee.getCustomFields();
        Map<String, String> customFieldsMap = transCustomFields(customFields);
        if (ObjectUtils.isEmpty(customFieldsMap)) {
            log.info("员工扩展属性为空");
            throw new OpenApiArgumentException("员工扩展属性为空");
        }
        String contractCompanyId = StringUtils.obj2str(customFieldsMap.get("contract_company_id1"));
        String kingdeeDepartmentId = StringUtils.obj2str(customFieldsMap.get("kingdee_department_id1"));
        String bankOfDeposit = StringUtils.obj2str(customFieldsMap.get("bank_of_deposit1"));
        String bankCardNo = StringUtils.obj2str(customFieldsMap.get("bank_card_no1"));
        String employeeNo = employee.getEmployeeNo();
        if (StringUtils.isBlank(contractCompanyId) || StringUtils.isBlank(kingdeeDepartmentId)
            || StringUtils.isBlank(bankOfDeposit) || StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(employeeNo)) {
            log.info("员工扩展属性缺失：{}", JsonUtils.toJson(customFields));
            throw new OpenApiArgumentException("员工扩展属性缺失");
        }
        KingdeeExpReimbursementDTO kingdeeExpReimbursementDTO = new KingdeeExpReimbursementDTO();
        KingdeeExpReimbursementDTO.Resource resource = new KingdeeExpReimbursementDTO.Resource();
        kingdeeExpReimbursementDTO.setData(resource);
        KingdeeExpReimbursementDTO.Resource.ModelDTO modelDTO = new KingdeeExpReimbursementDTO.Resource.ModelDTO();
        resource.setModel(modelDTO);
        //单据编号
        String billNo = data.getReimbId();
        modelDTO.setBillNo(billNo);
        //申请时间
        String date = DateUtils.toSimpleStr(DateUtils.now());
        modelDTO.setDate(date);
        //币种
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO currencyId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        currencyId.setNumber(Constant.RMB);
        modelDTO.setCurrencyid(currencyId);
        KingdeeExpReimbursementDTO.Resource.FNumberTDTO orgId = new KingdeeExpReimbursementDTO.Resource.FNumberTDTO();
        //申请组织，先从控件取，取不到从员工自定义字段取
        Optional<String> legalEntityIdOptional = Optional.ofNullable(data).map(RemiDetailResDTO::getEntity).map(ControlDTO::getId);
        String legalEntityThirdId=null;
        if(legalEntityIdOptional.isPresent()){
            //转三方法人主体id
            List<CommonIdDTO> commonIdDTOS = commonService.queryIdDTO(companyId,Lists.newArrayList(legalEntityIdOptional.get()), IdTypeEnums.FB_ID.getKey(), IdBusinessTypeEnums.LEGAL_ENTITY.getKey());
            if(ObjectUtils.isEmpty(commonIdDTOS)){
               log.info("法人实体转换失败，公司id:{},分贝通法人实体id:{}",companyId,data.getEntity().getId());
               throw new FinhubException(-9999, "法人实体转换失败,分贝通法人实体id:"+data.getEntity().getId());
            }
            legalEntityThirdId = commonIdDTOS.get(0).getThirdId();
            orgId.setNumber(legalEntityThirdId);
        }else{
            orgId.setNumber(contractCompanyId);
        }
        modelDTO.setOrgId(orgId);
        //事由
        String applyReason = data.getApplyReason() == null ? "" : data.getApplyReason();
        String applyReasonDesc = data.getApplyReasonDesc() == null ? "" : data.getApplyReasonDesc();
        if (StringUtils.isBlank(applyReason) || StringUtils.isBlank(applyReasonDesc)) {
            modelDTO.setCausa(applyReason + applyReasonDesc);
        } else {
            modelDTO.setCausa(applyReason + "-" + applyReasonDesc);
        }
        //申请人
        String proposerId = employeeNo;
        KingdeeExpReimbursementDTO.Resource.FSTAFFNUMBERDTO proposerDTO = new KingdeeExpReimbursementDTO.Resource.FSTAFFNUMBERDTO();
        proposerDTO.setStaffNumber(proposerId);
        modelDTO.setProposerId(proposerDTO);
        //申请部门
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO requestDeptDTO = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        //取金蝶部门编码，先从详情接口控件中取，取不到则从人员扩展字段中取

        requestDeptDTO.setNumber(kingdeeDepartmentId);
        modelDTO.setRequestDeptId(requestDeptDTO);
        //单据类型
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO billTypeId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        billTypeId.setNumber(Constant.REIMBURSEMENT);
        modelDTO.setBillTypeID(billTypeId);
        //费用承担组织
        KingdeeExpReimbursementDTO.Resource.FNumberTDTO expenseOrgId = new KingdeeExpReimbursementDTO.Resource.FNumberTDTO();
        expenseOrgId.setNumber(orgId.getNumber());
        modelDTO.setExpenseOrgId(expenseOrgId);
        //费用承担部门
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO expenseDeptId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        Optional<String> deptIdOptional = Optional.ofNullable(data).map(RemiDetailResDTO::getReimbDept).map(ControlDTO::getCode);
        if(deptIdOptional.isPresent()){
            expenseDeptId.setNumber(deptIdOptional.get());
        }else{
            expenseDeptId.setNumber(kingdeeDepartmentId);
        }
        modelDTO.setExpenseDeptId(expenseDeptId);
        //往来单位类型
        String contactUnitType = Constant.BD_EMPINFO;
        modelDTO.setContactunittype(contactUnitType);
        //往来单位
        KingdeeExpReimbursementDTO.Resource.FNumberTDTO contactUnit = new KingdeeExpReimbursementDTO.Resource.FNumberTDTO();
        contactUnit.setNumber(employeeNo);
        modelDTO.setContactunit(contactUnit);
        //付款组织
        KingdeeExpReimbursementDTO.Resource.FNumberTDTO payOrgId = new KingdeeExpReimbursementDTO.Resource.FNumberTDTO();
        if(!StringUtils.isTrimBlank(legalEntityThirdId)){
            payOrgId.setNumber(legalEntityThirdId);
        }else{
            payOrgId.setNumber(contractCompanyId);
        }
        modelDTO.setPayorgid(payOrgId);
        //结算方式
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO paySettlleTypeId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        paySettlleTypeId.setNumber(Constant.SETTLLE_TYPE_JSFS04_SYS);
        modelDTO.setPaySettlleTypeId(paySettlleTypeId);
        //开户行
        String bankBranch = bankOfDeposit;
        modelDTO.setBankBranch(bankBranch);
        //账户名称
        String bankAccountName = data.getUserName();
        modelDTO.setBankAccountName(bankAccountName);
        //银行账号
        String bankAccount = bankCardNo;
        modelDTO.setBankAccount(bankAccount);
        //本位币
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO locCurrencyId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        locCurrencyId.setNumber(Constant.RMB);
        modelDTO.setLocCurrenyId(locCurrencyId);
        //汇率类型 固定税率
        KingdeeExpReimbursementDTO.Resource.FNUMBERDTO exchangeTypeID = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
        exchangeTypeID.setNumber(Constant.FIXED_TAX_RATE);
        modelDTO.setExchangetype(exchangeTypeID);
        //税率
        modelDTO.setExchangeRate(new BigDecimal(1));
        //报销金额本位币(含税）
        BigDecimal locExpAmountSum = data.getTotalAmount();
        modelDTO.setLocExpAmountSum(locExpAmountSum);
        //退/付款金额本位币(含税）
        BigDecimal locReqAmountSum = data.getTotalAmount();
        modelDTO.setLocReqAmountSum(locReqAmountSum);
        //核定报销金额汇总(含税）
        BigDecimal expAmountSum = data.getTotalAmount();
        modelDTO.setExpAmountSum(expAmountSum);
        //核定退/付款金额汇总(含税）
        BigDecimal reqAmountSum = data.getTotalAmount();
        modelDTO.setReqAmountSum(reqAmountSum);
        //创建时间
        modelDTO.setCreateDate(date);
        //付款 1 退款 2
        String requestType = "1";
        modelDTO.setRequestType(requestType);
        //申请报销金额汇总(含税）
        BigDecimal reqReimbAmountSum = data.getTotalAmount();
        modelDTO.setReqReimbAmountSum(reqReimbAmountSum);
        //申请退/付款金额汇总(含税)
        BigDecimal reqPayReFoundAmountSum = data.getTotalAmount();
        modelDTO.setReqPayReFoundAmountSum(reqPayReFoundAmountSum);
        List<RemiCostResDTO> costList = data.getReimbExpense();
        List<KingdeeExpReimbursementDTO.Resource.FEntityDTO> entityList = Lists.newArrayList();
        modelDTO.setEntity(entityList);
        if (!ObjectUtils.isEmpty(costList)) {
            for (RemiCostResDTO thirdCostRes : costList) {
                KingdeeExpReimbursementDTO.Resource.FEntityDTO entityDTO = new KingdeeExpReimbursementDTO.Resource.FEntityDTO();
                entityList.add(entityDTO);
                //费用项目编码 跟事由挂钩
                KingdeeExpReimbursementDTO.Resource.FNUMBERDTO expId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
                expId.setNumber(thirdCostRes.getCostCategoryCustomCode());
                entityDTO.setExpId(expId);
                //税额本位币
                entityDTO.setLocTaxAmount(BigDecimal.ZERO);
                //不含税金额本位币（不含税）
                entityDTO.setLocNoTaxAmount(thirdCostRes.getTotalAmount());
                //费用金额（不含税）
                entityDTO.setTaxSubmitAmt(thirdCostRes.getTotalAmount());
                //费用承担部门
                KingdeeExpReimbursementDTO.Resource.FNUMBERDTO expenseDeptEntryId = new KingdeeExpReimbursementDTO.Resource.FNUMBERDTO();
                if(deptIdOptional.isPresent()){
                    expenseDeptEntryId.setNumber(deptIdOptional.get());
                }else{
                    expenseDeptEntryId.setNumber(kingdeeDepartmentId);
                }
                //税额
                entityDTO.setTaxAmt(BigDecimal.ZERO);
                //申请退/付款金额 含税
                entityDTO.setExpenseDeptEntryId(expenseDeptEntryId);
                //备注
                entityDTO.setRemark(thirdCostRes.getCostReason());
                //新品研发项目ID
                KingdeeExpReimbursementDTO.Resource.FNumberTDTO yuanAssistant =
                    new KingdeeExpReimbursementDTO.Resource.FNumberTDTO();
                yuanAssistant.setNumber("ADMIN");
                if(!ObjectUtils.isEmpty(thirdCostRes.getCostAttributionGroup())){
                    for(CostAttributionGroupDTO costAttributionGroup:thirdCostRes.getCostAttributionGroup()){
                        if(costAttributionGroup.getCategory()==2){
                            if(!ObjectUtils.isEmpty(costAttributionGroup.getCostAttributionList())){
                               String costAttributionCode =  costAttributionGroup.getCostAttributionList().get(0).getCostAttributionCode();
                               yuanAssistant.setNumber(costAttributionCode);
                            }
                        }
                    }
                }
                entityDTO.setYuanAssistant(yuanAssistant);
                //未税金额等扩展信息
                List<KVEntity> costCustomFields = thirdCostRes.getCostCustomFields();
                Map<String, String> reimbExpenseCustomFieldsMap = transCustomFields(costCustomFields);
                if (!ObjectUtils.isEmpty(reimbExpenseCustomFieldsMap) && !ObjectUtils.isEmpty(reimbExpenseCustomFieldsMap.get("未税金额")) && !ObjectUtils.isEmpty(reimbExpenseCustomFieldsMap.get("可抵扣税额"))) {
                    //可抵扣税额+未税金额=费用金额
                    BigDecimal noTaxAmount = BigDecimalUtils.obj2big(reimbExpenseCustomFieldsMap.get("未税金额"));
                    BigDecimal tax = BigDecimalUtils.obj2big(reimbExpenseCustomFieldsMap.get("可抵扣税额"));
                    //不含税金额本位币（不含税）
                    entityDTO.setLocNoTaxAmount(noTaxAmount);
                    //费用金额（不含税）
                    entityDTO.setTaxSubmitAmt(noTaxAmount);
                    //税额
                    entityDTO.setTaxAmt(tax);
                    entityDTO.setLocTaxAmount(tax);
                }
            }
        }
        return kingdeeExpReimbursementDTO;
    }

    private Map<String, String> transCustomFields(List<KVEntity> customFields) {
        Map<String, String> result = Maps.newHashMap();
        if (ObjectUtils.isEmpty(customFields)) {
            return result;
        }
        for (KVEntity entity : customFields) {
            result.put(entity.getKey(), StringUtils.obj2str(entity.getValue()));
        }
        return result;
    }
}
