package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>Title: MallOrderListReqDTO</p>
 * <p>Description: 采购订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 7:59 PM
 */
@Data
public class MallOrderListReqDTO {

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
     * 收货人姓名：文本框输入，精确匹配
     */
    @JsonProperty("consignee_name")
    private String consigneeName;

    /**
     * 收货人手机号：文本框输入，精确匹配
     */
    @JsonProperty("consignee_phone")
    private String consigneePhone;

    /**
     * 订单状态：详见采购状态码
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 起始页
     */
    @NotNull(message = "起始页[page_index]不可为空")
    @JsonProperty("page_index")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @NotNull(message = "每页显示的条数[page_size]不可为空")
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 订单创建开始时间 yyyy-mm-dd
     */
    @JsonProperty("create_time_begin")
    private String createTimeBegin;

    /**
     * 订单创建结束时间 yyyy-mm-dd
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
     * 订单类型1:因公;2:因私
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
