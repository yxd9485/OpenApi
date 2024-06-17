package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 汽车票列表结果DTO
 * @author zhangpeng
 * @date 2022/3/31 3:15 下午
 */
@Data
public class BusOrderListResDTO {

    @JsonProperty("root_order_id")
    private String rootOrderId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_state")
    private String orderState;

    @JsonProperty("apply_id")
    private String applyId;

    @JsonProperty("ticket_id")
    private String ticketId;

    @JsonProperty("ticket_state")
    private String ticketState;

    @JsonProperty("ticket_type")
    private String ticketType;

    @JsonProperty("order_type")
    private int orderType;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_phone")
    private String userPhone;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("passenger_name")
    private String passengerName;

    @JsonProperty("passenger_phone")
    private String passengerPhone;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("total_price")
    private String totalPrice;

    private Trip trip;

    @Data
    public static class Trip{
        @JsonProperty("from_station_name")
        private String fromStationName;

        @JsonProperty("to_station_name")
        private String toStationName;

        @JsonProperty("from_city_name")
        private String fromCityName;

        @JsonProperty("from_city_id")
        private String fromCityId;

        @JsonProperty("to_city_name")
        private String toCityName;

        @JsonProperty("to_city_id")
        private String toCityId;

        @JsonProperty("bus_no")
        private String busNo;

        @JsonProperty("start_time")
        private String startTime;
    }

}
