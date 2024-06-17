package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-10-22 16:52:52
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuIsvGetOrderRespDTO {

    private Integer code;

    private String msg;

    private OrderData data;

    @Data
    public static class OrderData {

        private Order order;

    }

    @Data
    public static class Order {

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("price_plan_id")
        private String pricePlanId;

        @JsonProperty("price_plan_type")
        private String pricePlanType;

        private Integer seats;

        @JsonProperty("buy_count")
        private Integer buyCount;

        @JsonProperty("create_time")
        private String createTime;

        @JsonProperty("pay_time")
        private String payTime;

        private String status;

        @JsonProperty("buy_type")
        private String buyType;

        @JsonProperty("src_order_id")
        private String srcOrderId;

        @JsonProperty("dst_order_id")
        private String dstOrderId;

        @JsonProperty("order_pay_price")
        private Integer orderPayPrice;

        @JsonProperty("tenant_key")
        private String tenantKey;

    }
}