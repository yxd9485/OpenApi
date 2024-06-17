package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName ApplyDetailDTO
 * @Description 2.0申请单内容
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDetailDTO {

    /**
     * 单据编号
     */
    @JsonProperty("apply_id")
    private String applyId;

    /**
     * 员工三方id
     */
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    /**
     * 预估费用总额，单位为元
     */
    @JsonProperty("estimated_total_amount")
    private BigDecimal estimatedTotalAmount;

    /**
     * 申请单名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 付款时间
     */
    @JsonProperty("payment_time")
    private String paymentTime;

    /**
     * 付款用途
     */
    @JsonProperty("payment_use")
    private String paymentUse;

    /**
     * 申请事由
     */
    @JsonProperty("apply_reason")
    private String applyReason;

}
