package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: OrderPriceInfoDTO</p>
 * <p>Description: 订单金额信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/2 5:21 PM
 */
@Data
public class OrderPriceInfoDTO {

    /**
     * 订单总价
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * 公司支付总金额
     */
    @JsonProperty("company_total_pay")
    private BigDecimal companyTotalPay;

    /**
     * 个人支付总金额
     */
    @JsonProperty("personal_total_pay")
    private BigDecimal personalTotalPay;

    /**
     * 分贝卷支付金额
     */
    @JsonProperty("fbq_pay_price")
    private BigDecimal fbqPayPrice;

    /**
     * 分贝券使用数量
     */
    @JsonProperty("fbq_usage_num")
    private Integer fbqUsageNum;

    /**
     * 分贝币支付金额
     */
    @JsonProperty("fbb_pay_price")
    private BigDecimal fbbPayPrice;

    /**
     * 第三方支付金额
     */
    @JsonProperty("third_payment_price")
    private BigDecimal thirdPaymentPrice;

    /**
     * 第三方支付类型
     */
    @JsonProperty("third_pay_channel")
    private String thirdPayChannel;

    /**
     * 企业优惠券金额
     */
    @JsonProperty("coupon_amount")
    private BigDecimal couponAmount;

    /**
     * 红包券金额
     */
    @JsonProperty("red_envelope")
    private BigDecimal redEnvelope;
}
