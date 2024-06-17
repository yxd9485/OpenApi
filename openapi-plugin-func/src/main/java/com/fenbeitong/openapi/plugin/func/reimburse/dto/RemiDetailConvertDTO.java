package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.finhub.common.saas.entity.CostAttributionGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName RemiDetailResDTo
 * @Description 报销单明细查询返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/20 上午8:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemiDetailConvertDTO {
    @JsonProperty("reimb_id")
    private String reimbId;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("company_id")
    private String companyId;
    @JsonProperty("apply_state")
    private Integer applyState;
    @JsonProperty("proposor_id")
    private String proposorId;
    @JsonProperty("proposor_name")
    private String proposorName;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("currency_code")
    private String currencyCode="CNY";//币种编码，固定CNY
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    @JsonProperty("payment_amount")
    private BigDecimal paymentAmount;
    @JsonProperty("no_need_pay_amount")
    private BigDecimal noNeedPayAmount;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("reimburse_time")
    private String reimburseTime;
    @JsonProperty("payment_time")
    private String paymentTime;
    @JsonProperty("apply_reason")
    private String applyReason;
    @JsonProperty("apply_reason_id")
    private String applyReasonId;
    @JsonProperty("apply_reason_desc")
    private String applyReasonDesc;
    @JsonProperty("expenses_num")
    private Integer expensesNum;
    @JsonProperty("receipt_num")
    private Integer receiptNum;
    @JsonProperty("receipt_total_amount")
    private BigDecimal receiptTotalAmount;
    @JsonProperty("payment_status")
    private Integer paymentStatus;
    @JsonProperty("ticket_status")
    private Integer ticketStatus;//单据回票状态（转换）
    @JsonProperty("form_id")
    private String formId;
    @JsonProperty("relevance_application")
    private List<RelevanceInfoDTO> relevanceApplication;
    @JsonProperty("relevance_application_num")
    private  Integer relevanceApplicationNum;
    @JsonProperty("apply_info")
    private List<ThirdApplyLogResDTO> applyInfo;//审批日志，需转换
    @JsonProperty("form_custom_fields")
    private List<KVEntity> formCustomFields;
    @JsonProperty("reimb_expense")
    private List<RemiCostResDTO> reimbExpense;

    @JsonProperty("pay_finish_time")
    private String payFinishTime;
    @JsonProperty("employee_info")
    private List<EmployeeInfoDTO> employeeInfo;//人员信息
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    @JsonProperty("employee_number")
    private String employeeNumber;
    //提单人信息
    @JsonProperty("proposor_phone")
    private String proposorPhone;
    @JsonProperty("proposor_department_id")
    private String proposorDepartmentId;
    @JsonProperty("proposor_department_name")
    private String proposorDepartmentName;
    @JsonProperty("proposor_custom_fields_str")
    private String proposorCustomFieldsStr;
    @JsonProperty("proposor_custom_fields")
    private List<KVEntity> proposorCustomFields;
    @JsonProperty("third_proposor_department_id")
    private String thirdProposorDepartmentId;
    //报销人信息
    @JsonProperty("user_phone")
    private String userPhone;
    @JsonProperty("user_department_id")
    private String userDepartmentId;
    @JsonProperty("user_department_name")
    private String userDepartmentName;
    @JsonProperty("user_custom_fields_str")
    private String userCustomFieldsStr;
    @JsonProperty("user_custom_fields")
    private List<KVEntity> userCustomFields;
    @JsonProperty("third_user_department_id")
    private String thirdUserDepartmentId;
    @JsonProperty("user_third_employee_id")
    private String userThirdEmployeeId;
    @JsonProperty("user_employee_number")
    private String userEmployeeNumber;

    //关联申请单的三方ID
    @JsonProperty("third_apply_id")
    private String thirdApplyId;
    //费用归属三方ID（部门）
    @JsonProperty("third_department_id")
    private String thirdDepartmentId;
    //费用归属三方ID（项目）
    @JsonProperty("third_project_id")
    private String thirdProjectId;
    @JsonProperty("cost_list")
    private List<CostAttributionGroup> costList;
    @JsonProperty("entity")
    private ControlDTO entity;
    @JsonProperty("reimb_dept")
    private ControlDTO reimbDept;

}
