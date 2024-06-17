package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: ExpressDeliveryOrderListQueryReq</p>
 * <p>Description: 快递订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/3 7:59 PM
 */
@Data
public class ReliefOrderListReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 预订人姓名：文本框输入，精确匹配
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 预订人手机号：文本框输入，精确匹配
     */
    @JsonProperty("user_phone")
    private String userPhone;

    /**
     * 消费人的手机号：文本框输入，精确匹配
     */
    @JsonProperty("consumer_phone")
    private String consumerPhone;

    /**
     * 消费人的人名称：文本框输入，精确匹配
     */
    @JsonProperty("consumer_name")
    private String consumerName;

    /**
     * 订单状态：
     * 80, "已减免"
     * 256, "已删除"
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
     * 订单类型（具体场景）
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
