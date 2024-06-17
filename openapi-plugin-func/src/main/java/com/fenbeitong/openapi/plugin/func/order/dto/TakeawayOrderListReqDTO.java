package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

/**
 * <p>Title: TakeawayOrderListReqDTO</p>
 * <p>Description: 外卖订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/20 10:59 AM
 */
@Data
public class TakeawayOrderListReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 订单Id
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 订单的状态
     */
    @JsonProperty("status")
    private Integer status;

    @JsonProperty("refund_order_id")
    private String refundOrderId;

    /**
     * 预定人姓名
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 预定人手机号
     */
    @JsonProperty("user_phone")
    private String userPhone;


    /**
     * 外卖订单人名称
     */
    @JsonProperty("consignee_name")
    private String consigneeName;


    /**
     * 起始页
     */
    @JsonProperty("page_index")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 订单开始时间 yyyy-mm-dd
     */
    @JsonProperty("create_time_begin")
    private String createTimeBegin;

    /**
     * 订单结束时间 yyyy-mm-dd
     */
    @JsonProperty("create_time_end")
    private String createTimeEnd;

    /**
     * 订单类型1:因公;2:因私
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;

}
