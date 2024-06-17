

package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

/**
 * <p>Title: TrainOrderListReqDTO</p>
 * <p>Description: 火车订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/17 4:59 PM
 */
@Data
public class TrainOrderListReqDTO {

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
     * 预定人姓名
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 订单的状态
     */
    @JsonProperty("status")
    private Integer status;


    /**
     * 预定人手机号
     */
    @JsonProperty("user_phone")
    private String userPhone;


    /**
     * 乘客名称
     */
    @JsonProperty("passenger_name")
    private String passengerName;

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


