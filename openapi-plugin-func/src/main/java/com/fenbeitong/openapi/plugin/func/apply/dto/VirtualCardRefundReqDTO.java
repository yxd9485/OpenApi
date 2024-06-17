package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 虚拟卡额度退还
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class VirtualCardRefundReqDTO {

    /**
     * 员工
     */
    @JsonProperty("third_employee_id")
    @NotBlank(message = "员工id【third_employee_id】不可为空")
    private String thirdEmployeeId;


    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 申请事由
     */
    @JsonProperty("apply_reason")
    private String applyReason;

    /**
     * 事由
     */
    @JsonProperty("apply_reason_desc")
    private String applyReasonDesc;


    /**
     * 退还额度 单位分
     */
    @JsonProperty("refund_credit_amount")
    private BigDecimal refundCreditAmount;

}
