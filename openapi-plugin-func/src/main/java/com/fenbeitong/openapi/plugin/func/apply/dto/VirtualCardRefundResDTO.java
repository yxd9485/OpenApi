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
public class VirtualCardRefundResDTO {

    /**
     * 员工
     */
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    /**
     * 银行卡号
     */
    @JsonProperty("bank_account_no")
    private String bankAccountNo;

    /**
     * 退还额度 单位分
     */
    @JsonProperty("refund_amount")
    private BigDecimal refundAmount;

}
