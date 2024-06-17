package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 汽车票订单列表DTO
 * @author zhangpeng
 * @date 2022/3/30 3:04 下午
 */
@Data
public class BusOrderListReqDTO {

    @JsonProperty("root_order_id")
    private String rootOrderId;

    @JsonProperty("order_state")
    private String orderState;

    @JsonProperty("create_time_begin")
    private String createTimeBegin;

    @JsonProperty("create_time_end")
    private String createTimeEnd;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_phone")
    private String userPhone;

    @JsonProperty("start_date_from")
    private String startDateFrom;

    @JsonProperty("end_date_from")
    private String endDateFrom;

    @JsonProperty("passenger_name")
    private String passengerName;

    @JsonProperty("passenger_phone")
    private String passengerPhone;

    @JsonProperty("bus_no")
    private String busNo;

    @JsonProperty("cost_attribution_name")
    private String costAttributionName;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("page_index")
    @NotNull(message = "起始页[page_index]不可为空")
    private Integer pageIndex;

    @JsonProperty("page_size")
    @NotNull(message = "每页显示的条数[page_size]不可为空")
    private Integer pageSize;

    private String companyId;
}
