package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.CostCenterProductEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.CostCenterChannelEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.ExpenseTypeEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.PayTypeEnum;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustomReimbursementDto;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustomReimbursementNoticeDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.employee.service.IGetEmployeeInfoFromUcService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.model.po.orgunit.OrgUnit;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.weaver.v8.workflow.WorkflowMainTableInfo;
import com.weaver.v8.workflow.WorkflowRequestTableField;
import com.weaver.v8.workflow.WorkflowRequestTableRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 泛微报销单主子表构建
 * @Auther zhang.peng
 * @Date 2022/2/20
 */
@Slf4j
@Service
public class WorkFlowReimbursementTableFieldBuilder extends WorkFlowRequestTableFieldBuilder {

    @Autowired
    private IGetEmployeeInfoFromUcService getEmployeeInfoFromUcService;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    private static final String MAIN_TABLE = "main";
    private static final String DETAIL_TABLE = "detail";

    /**
     * 主表
     * 成本中心（渠道）
     * 成本中心（产品）
     * 报销事由
     * 报销总额
     * 付款方式
     * 备用金审批单
     * 出差申请
     * 事前审批单
     * 报销人
     * 报销人银行账号
     * 报销类型
     */
    private static final String COST_CENTER_CHANNEL = "cost_center_channel";
    private static final String COST_CENTER_PRODUCT = "cost_center_product";
    private static final String REIMBURSE_REASON = "reimburse_reason";
    private static final String REIMBURSE_AMOUNT = "reimburse_amount";
    private static final String PAY_TYPE = "pay_type";
    private static final String BAK_APPROVE = "bak_approve";
    private static final String TRIP_APPROVE = "trip_approve";
    private static final String THIRD_SYSTEM_APPROVE = "third_system_approve";
    private static final String BEFORE_APPROVE = "before_approve";
    private static final String REIMBURSE_PERSON = "reimburse_person";
    private static final String REIMBURSE_BANK_ACCOUNT = "reimburse_bank_account";
    private static final String APPLY_TIME = "apply_time";
    private static final String APPLY_PERSON = "apply_person";
    private static final String APPLY_DEPARTMENT = "apply_department";
    private static final String BELONG_DEPARTMENT = "belong_department";
    private static final String FBT_APPROVE_ID = "fbt_approve_id";
    private static final String COST_DEPARTMENT = "cost_department";
    private static final String EXPENSE_TYPE = "expense_type";
    // 差旅补助金额
    private static final String TRIP_ALLOWANCE_AMOUNT = "trip_allowance_amount";
    // 事前审批单截图
    private static final String BEFORE_TRIP_APPROVE_PICTURE = "before_trip_approve_picture";

    public WorkflowMainTableInfo buildReimburseMainTableInfo(FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId , List<OpenEtlMappingConfig> mappingConfigList){

        WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
        WorkflowRequestTableRecord[] workflowRequestTableRecord = new WorkflowRequestTableRecord[1];//主表字段只有一条记录

        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        mappingConfigList = mappingConfigList.stream().filter(config -> MAIN_TABLE.equals(config.getGroupName())).collect(Collectors.toList());
        if (CollectionUtils.isBlank(mappingConfigList)){
            return workflowMainTableInfo;
        }
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[mappingConfigList.size()];//主的字段

        CustomReimbursementDto customReimbursementDto = ( CustomReimbursementDto )fenbeitongApproveDto;
        if ( null != customReimbursementDto.getEmployee() ){
            CustomReimbursementNoticeDTO.Employee employee = customReimbursementDto.getEmployee();
            fenbeitongApproveDto.setThirdEmployeeId(employee.getEmployeeThirdId());
            fenbeitongApproveDto.setThirdOrgId(employee.getDeptId());
            fenbeitongApproveDto.setThirdDepartmentId(employee.getDeptId());
        }
        buildReimburseFields(mappingConfigList,WorkflowRequestTableField,fenbeitongApproveDto,fbtApproveId);

        workflowRequestTableRecord[0] = new WorkflowRequestTableRecord();
        workflowRequestTableRecord[0].setWorkflowRequestTableFields(WorkflowRequestTableField);
        workflowMainTableInfo.setRequestRecords(workflowRequestTableRecord);

        return workflowMainTableInfo;
    }

    public void buildReimburseFields(List<OpenEtlMappingConfig> mappingConfigList , WorkflowRequestTableField[] WorkflowRequestTableField , FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId ){
        CustomReimbursementDto customReimbursementDto = (CustomReimbursementDto) fenbeitongApproveDto;
        List<CustomReimbursementNoticeDTO.CustomControl> customControls = customReimbursementDto.getCustomControls();
        Map<String,Object> customControlsMap = new HashMap<>();
        if (CollectionUtils.isNotBlank(customControls)){
            customControlsMap = customControls.stream().collect(Collectors.toMap(CustomReimbursementNoticeDTO.CustomControl::getName,CustomReimbursementNoticeDTO.CustomControl::getDetail));
        }
        log.info("主表 customControlsMap : {}",JsonUtils.toJson(customControlsMap));
        for (int i = 0; i < mappingConfigList.size(); i++) {
            WorkflowRequestTableField[i] = new WorkflowRequestTableField();
            OpenEtlMappingConfig config = mappingConfigList.get(i);
            String targetName = config.getSrcCol();
            WorkflowRequestTableField[i].setFieldName(config.getTgtCol());//姓名
            try {
                if (COST_CENTER_CHANNEL.equals(targetName)){
                    int value = CostCenterChannelEnum.getType(getValue(customControlsMap,"成本中心(渠道维度)")).getType();
                    WorkflowRequestTableField[i].setFieldValue(value+"");
                }
                if (COST_CENTER_PRODUCT.equals(targetName)){
                    int value = CostCenterProductEnum.getType(getValue(customControlsMap,"成本中心(产品维度)")).getType();
                    WorkflowRequestTableField[i].setFieldValue(value+"");
                }
                if (REIMBURSE_REASON.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue((String)customControlsMap.get("报销事由"));
                }
                if (REIMBURSE_AMOUNT.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue( "0".equals(customReimbursementDto.getActualReceivableAmount()) ? customReimbursementDto.getPublicAmount() : customReimbursementDto.getActualReceivableAmount());
                }
                if (PAY_TYPE.equals(targetName)){
                    int value = PayTypeEnum.getPayType(getValue(customControlsMap,"支付方式")).getType();
                    WorkflowRequestTableField[i].setFieldValue(value+"");
                }
                if (REIMBURSE_PERSON.equals(targetName)){
                    Map result = JsonUtils.toObj((String)customControlsMap.get("收款人姓名"),Map.class);
                    String employeeId = (String) result.get("personnelUserId");
                    ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfoFromUcService.getEmployInfoByEmployeeId(fenbeitongApproveDto.getCompanyId(),employeeId,"0");
                    WorkflowRequestTableField[i].setFieldValue( null == thirdEmployeeRes ? "" : thirdEmployeeRes.getEmployee().getThirdEmployeeId());
                }
                if (REIMBURSE_BANK_ACCOUNT.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue((String)customControlsMap.get("收款人银行账号"));
                }
                if (APPLY_TIME.equals(targetName)){
                    String applyTime = DateUtils.toStr(DateUtils.toDate(customReimbursementDto.getApplyTime()),"yyyy-MM-dd");
                    WorkflowRequestTableField[i].setFieldValue(applyTime);
                }
                CustomReimbursementNoticeDTO.Employee employee = customReimbursementDto.getEmployee();
                if (APPLY_PERSON.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue( null == employee ? "" : employee.getEmployeeThirdId());
                }
                if (APPLY_DEPARTMENT.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue(null == employee ? "" : employee.getDeptId());
                }
                if (BELONG_DEPARTMENT.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue(null == employee ? "" : employee.getDeptId());
                }
                if (TRIP_APPROVE.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue((String)customControlsMap.get("出差申请"));
                }
                if (THIRD_SYSTEM_APPROVE.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue((String)customControlsMap.get("外部审批单"));
                }
                if (BAK_APPROVE.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue((String)customControlsMap.get("备用金审批单"));
                }
                if (FBT_APPROVE_ID.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue(fbtApproveId);
                }
                if (BEFORE_TRIP_APPROVE_PICTURE.equals(targetName)){
                    String pictureUrl = (String) customControlsMap.get("附件");
                    String[] pictures = JsonUtils.toObj(pictureUrl,String[].class);
                    FileUrlValueBuilder.buildFileUrls( ObjectUtils.isEmpty(pictures) ? "" : pictures[0],WorkflowRequestTableField[i]);
                }
                if (COST_DEPARTMENT.equals(targetName)){
                    String orgUnitId = getValueFromList(customControlsMap,"费用承担部门");
                    OrgUnit orgUnit = orgUnitService.getOrgUnitById(orgUnitId);
                    log.info("三方组织架构 : {}" , JsonUtils.toJson(orgUnit));
                    WorkflowRequestTableField[i].setFieldValue(orgUnit.getThird_org_id());
                }
                if (EXPENSE_TYPE.equals(targetName)){
                    int value = ExpenseTypeEnum.getType(getValue(customControlsMap,"报销类型")).getType();
                    WorkflowRequestTableField[i].setFieldValue(value+"");
                }
                if (TRIP_ALLOWANCE_AMOUNT.equals(targetName)){
                    WorkflowRequestTableField[i].setFieldValue((String)customControlsMap.get("差旅补助总额"));
                }
            } catch (Exception e) {
                log.info("转换字段失败 : {} , 字段 {}" , e.getMessage() , targetName);
            }
            WorkflowRequestTableField[i].setView(true);//字段是否可见
            WorkflowRequestTableField[i].setEdit(true);//字段是否可编辑
        }
    }

    public static String getValue(Map<String,Object> customControlsMap , String keyName ){
        Map result = JsonUtils.toObj((String)customControlsMap.get(keyName),Map.class);
        return (String) result.get("value");
    }

    public static String getValueFromList(Map<String,Object> customControlsMap , String keyName ){
        List result = JsonUtils.toObj((String)customControlsMap.get(keyName),List.class);
        if (CollectionUtils.isBlank(result)){
            return "";
        }
        Object data = result.get(0);
        if ( data instanceof Map ){
            return (String) ((Map) data).get("id");
        }
        return "";
    }

}
