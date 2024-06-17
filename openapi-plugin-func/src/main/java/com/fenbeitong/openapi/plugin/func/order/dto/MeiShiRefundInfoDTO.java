package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: MeiShiRefundInfoDTO</p>
 * <p>Description: 美食退款订单信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/3 2:33 PM
 */
@Data
public class MeiShiRefundInfoDTO {

    @JsonProperty("refund_order_id")
    private String refundOrderId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("biz_type")
    private String bizType;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("pay_time")
    private String payTime;

    @JsonProperty("refund_time")
    private String refundTime;

    @JsonProperty("goods_name")
    private String goodsName;

    @JsonProperty("refund_status")
    private Integer refundStatus;

    @JsonProperty("refund_status_name")
    private String refundStatusName;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("supplier_order_id")
    private String supplierOrderId;
}
