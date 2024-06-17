package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author machao
 * @date 2022/9/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReimbursementDetailDTO {
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
    /**
     * 实际报销人id
     */
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("currency_code")
    private String currencyCode = "CNY";//币种编码，固定CNY
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
    private Integer relevanceApplicationNum;
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
    @JsonProperty("third_apply_id")
    private String thirdApplyId;
    @JsonProperty("entity")
    private ControlDTO entity;
    @JsonProperty("reimb_dept")
    private ControlDTO reimbDept;
}
