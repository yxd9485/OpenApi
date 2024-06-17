package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 汽车票详情结果DTO
 * @author zhangpeng
 * @date 2022/3/31 3:14 下午
 */
@Data
public class BusOrderDetailResDTO {

    @JsonProperty("root_order_id")
    private String rootOrderId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_type")
    private int orderType;

    @JsonProperty("order_state")
    private String orderState;

    @JsonProperty("ticket_id")
    private String ticketId;

    @JsonProperty("ticket_no")
    private String ticketNo;

    @JsonProperty("ticket_type")
    private String ticketType;

    @JsonProperty("ticket_state")
    private String ticketState;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("seat_no")
    private String seatNo;

    @JsonProperty("total_price")
    private String totalPrice;

    @JsonProperty("ticket_price")
    private String ticketPrice;

    @JsonProperty("service_price")
    private String servicePrice;

    @JsonProperty("coupon_mount")
    private String couponMount;

    @JsonProperty("refund_price")
    private String refundPrice;

    @JsonProperty("saas_info")
    private SaaS saasInfo;

    private Contacter contacter;
    private List<User> users;
    private Payer payer;
    private Trip trip;

    @Data
    public static class Payer{
        private String id;
        @JsonProperty("third_id")
        private String thirdId;

        private String name;
        private String phone;
        @JsonProperty("department_name")
        private String departmentName;

        @JsonProperty("department_id")
        private String departmentId;

        @JsonProperty("third_department_id")
        private String thirdDepartmentId;
    }

    @Data
    public static class User{
        private String id;
        @JsonProperty("third_id")
        private String thirdId;

        private String name;
        private String phone;
        @JsonProperty("department_name")
        private String departmentName;

        @JsonProperty("department_id")
        private String departmentId;

        @JsonProperty("third_department_id")
        private String thirdDepartmentId;

        @JsonProperty("certificate_type")
        private String certificateType;

        @JsonProperty("certificate_no")
        private String certificateNo;
    }

    @Data
    public static class Contacter{
        private String name;
        private String phone;
    }

    @Data
    public static class Trip{
        /**
         * 出发车站
         */
        @JsonProperty("from_station_name")
        private String fromStationName;

        /**
         * 到达车站
         */
        @JsonProperty("to_station_name")
        private String toStationName;

        /**
         * 出发城市
         */
        @JsonProperty("from_city_name")
        private String fromCityName;

        /**
         * 出发城市ID
         */
        @JsonProperty("from_city_id")
        private String fromCityId;

        /**
         * 到达城市
         */
        @JsonProperty("to_city_name")
        private String toCityName;

        /**
         * 到达城市ID
         */
        @JsonProperty("to_city_id")
        private String toCityId;

        /**
         * 车次
         */
        @JsonProperty("bus_no")
        private String busNo;

        /**
         * 出发时间
         */
        @JsonProperty("start_time")
        private String startTime;

    }

    @Data
    public static class SaaS{
        @JsonProperty("order_reason")
        private String orderReason;

        @JsonProperty("order_reason_desc")
        private String orderReasonDesc;

        @JsonProperty("order_remark_ext")
        private List<OrderRemarkExt> orderRemarkExt;

        private boolean exceed;

        @JsonProperty("exceedItem")
        private String exceedItem;

        @JsonProperty("exceed_reason")
        private String exceedReason;

        @JsonProperty("exceed_reason_desc")
        private String exceedReasonDesc;

        @JsonProperty("exceed_remark_ext")
        private String exceedRemarkExt;

        @JsonProperty("during_apply_id")
        private String duringApplyId;

        @JsonProperty("apply_id")
        private String applyId;

        @JsonProperty("cost_attributions")
        private List<CostAttribution> costAttributions;
    }

    @Data
    public static class OrderRemarkExt{
        private String title;
        private String detail;
    }

    @Data
    public static class CostAttribution{
        private String type;
        @JsonProperty("archive_id")
        private String archiveId;

        @JsonProperty("archive_name")
        private String archiveName;

        private List<Detail> details;
    }

    @Data
    public static class Detail{
        private String id;
        private String name;
        private String weight;
        private String amount;
    }
}
