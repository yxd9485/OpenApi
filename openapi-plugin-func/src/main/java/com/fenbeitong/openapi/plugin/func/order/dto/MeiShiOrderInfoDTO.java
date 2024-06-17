package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: MeiShiOrderInfoDTO</p>
 * <p>Description: 美食订单信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/3 2:25 PM
 */
@Data
public class MeiShiOrderInfoDTO {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("biz_type")
    private String bizType;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("pay_time")
    private String payTime;

    @JsonProperty("goods_name")
    private String goodsName;

    @JsonProperty("refund_status")
    private Integer refundStatus;

    @JsonProperty("refund_status_name")
    private String refundStatusName;

    @JsonProperty("shop_name")
    private String shopName;

    /**
     * 订单状态 1 待付款 82 已取消 81 已关闭 2 已付款 80 完成 21 发货失败
     */
    private Integer status;

    /**
     * 订单状态 1 待付款 82 已取消 81 已关闭 2 已付款 80 完成 21 发货失败
     */
    @JsonProperty("status_name")
    private String statusName;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("supplier_order_id")
    private String supplierOrderId;
}
