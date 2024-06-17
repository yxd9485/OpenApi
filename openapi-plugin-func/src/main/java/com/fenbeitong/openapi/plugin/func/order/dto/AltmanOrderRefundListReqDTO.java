package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AltmanOrderRefundListReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 退款单编号：文本框输入，精确匹配
     */
    @JsonProperty("refund_order_id")
    private String refundOrderId;

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 预订人姓名：文本框输入，精确匹配
     */
    @JsonProperty("consumer_name")
    private String consumerName;

    /**
     * 预订人手机号：文本框输入，精确匹配
     */
    @JsonProperty("consumer_phone")
    private String consumerPhone;

    /**
     *
     */
    @JsonProperty("status_list")
    private List<Integer> statusList;

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
     * 订单完成开始时间 yyyy-mm-dd
     */
    @JsonProperty("finish_time_begin")
    private String finishTimeBegin;

    /**
     * 订单完成结束时间 yyyy-mm-dd
     */
    @JsonProperty("finish_time_end")
    private String finishTimeEnd;

    /**
     * api版本号
     */
    private String apiVersion;
    /**
     * 订单类型（具体场景）
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
