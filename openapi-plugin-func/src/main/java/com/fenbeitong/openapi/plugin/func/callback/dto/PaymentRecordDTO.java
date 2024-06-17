package com.fenbeitong.openapi.plugin.func.callback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 付款结果通知
 *
 * @author ctl
 * @date 2022/8/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecordDTO implements Serializable {

    /**
     * 企业id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 付款单id
     */
    @JsonProperty("payment_id")
    private String paymentId;

    /**
     * 付款单三方id
     */
    @JsonProperty("third_payment_id")
    private String thirdPaymentId;

    /**
     * 付款单状态
     * 80：交易成功
     * 21：交易失败
     * 84：退回汇款
     */
    @JsonProperty("payment_state")
    private Integer paymentState;

    /**
     * 退回汇款标识
     * 0：否
     * 1：是
     */
    @JsonProperty("return_remittance")
    private Integer returnRemittance;

    /**
     * 失败、退回汇款原因
     */
    @JsonProperty("fail_reason")
    private String failReason;

    /**
     * 申请单id
     */
    @JsonProperty("apply_id")
    private String applyId;

    /**
     * 申请单三方id
     */
    @JsonProperty("third_apply_id")
    private String thirdApplyId;

    /**
     * 付款人id
     */
    @JsonProperty("payer_id")
    private String payerId;

    /**
     * 付款人三方id
     */
    @JsonProperty("third_payer_id")
    private String thirdPayerId;

    /**
     * 金额
     */
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * 付款时间
     */
    @JsonProperty("payment_time")
    private String paymentTime;
}
