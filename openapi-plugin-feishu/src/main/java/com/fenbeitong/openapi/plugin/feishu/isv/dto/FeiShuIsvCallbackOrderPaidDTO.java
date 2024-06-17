package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书应用商店应用购买回调
 *
 * @author lizhen
 * @date 2020/10/22
 */
@Data
public class FeiShuIsvCallbackOrderPaidDTO {

    private String uuid;

    private String token;

    private String ts;

    private String type;

    private Event event;

    @Data
    public static class Event {

        private String type;

        @JsonProperty("app_id")
        private String appId;

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

        @JsonProperty("buy_type")
        private String buyType;

        @JsonProperty("src_order_id")
        private String srcOrderId;

        @JsonProperty("order_pay_price")
        private Integer orderPayPrice;

        @JsonProperty("tenant_key")
        private String tenantKey;

    }
}
