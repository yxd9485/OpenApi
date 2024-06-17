package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>Title: AirOrderListReqDTO</p>
 * <p>Description: 机票订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 7:59 PM
 */
@Data
public class AirOrderListReqDTO {

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
     * 乘客姓名：文本框输入，精确匹配
     */
    @JsonProperty("passenger_name")
    private String passengerName;

    /**
     * 是否国际机票
     */
    @JsonProperty("is_intl")
    private Boolean isIntl;

    /**
     * 订单状态：详见机票状态码
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
     * 出发日期从 yyyy-mm-dd
     */
    @JsonProperty("starting_date_from")
    private String startingDateFrom;

    /**
     * 出发日期到 yyyy-mm-dd
     */
    @JsonProperty("starting_date_to")
    private String startingDateTo;

    /**
     * 审批单id
     */
    @JsonProperty("apply_id")
    private String applyId;

    /**
     * 审批单类型，0为分贝通审批单，1为第三方审批单
     */
    @JsonProperty("apply_type")
    private Integer applyType;

    /**
     * 下单人用户ID
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 下单人用户类型，0为分贝通，1为第三方审批单
     */
    @JsonProperty("user_type")
    private Integer userType;

    /**
     * 订单类别：1：因公  2：因私
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
