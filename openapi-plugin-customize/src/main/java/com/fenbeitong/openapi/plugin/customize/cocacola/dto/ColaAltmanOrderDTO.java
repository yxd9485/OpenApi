package com.fenbeitong.openapi.plugin.customize.cocacola.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可乐万能订单实体
 *
 * @author ctl
 * @date 2021/11/19
 */
@NoArgsConstructor
@Data
public class ColaAltmanOrderDTO implements Serializable {

    /**
     * 企业名称
     */
    @JsonProperty("company_name")
    private String companyName;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private String createTime;

    /**
     * 订单来源
     */
    @JsonProperty("order_channel")
    private String orderChannelDesc;

    /**
     * 订单状态
     */
    @JsonProperty("order_status_desc")
    private String orderStatusDesc;

    /**
     * 业务类别名称
     */
    @JsonProperty("biz_classify_name")
    private String bizClassifyName;

    /**
     * 业务名称
     */
    @JsonProperty("biz_name")
    private String bizName;

    /**
     * 订单总价
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * 订单采购价
     */
    @JsonProperty("cost_price")
    private BigDecimal costPrice;

    /**
     * 订单类型
     */
    @JsonProperty("order_type_desc")
    private String orderTypeDesc;

    /**
     * 主订单号
     */
    @JsonProperty("main_orderId")
    private String mainOrderId;

    /**
     * 订单号（代打对应的火车票订单号）
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 票id
     */
    @JsonProperty("ticket_id")
    private String ticketId;

    /**
     * 取票号
     */
    @JsonProperty("ticket_no")
    private String ticketNo;

    /**
     * 旅客姓名
     */
    @JsonProperty("consumer_name")
    private String consumerName;

    /**
     * 行程
     */
    @JsonProperty("travel")
    private String travel;

    /**
     * 车次
     */
    @JsonProperty("train_no")
    private String trainNo;

    /**
     * 出发时间
     */
    @JsonProperty("travel_start_time")
    private String travelStartTime;

    /**
     * 代打费用
     */
    @JsonProperty("proxy_fee")
    private BigDecimal proxyFee;

    /**
     * 代打服务费
     */
    @JsonProperty("proxy_service_fee")
    private BigDecimal proxyServiceFee;

    /**
     * ce单号 (通过票id查询出的行程审批三方id)
     */
    @JsonProperty("ce_id")
    private String ceId;

    /**
     * 直接导入的ce单号
     */
    @JsonProperty("import_ce_id")
    private String importCeId;
}
