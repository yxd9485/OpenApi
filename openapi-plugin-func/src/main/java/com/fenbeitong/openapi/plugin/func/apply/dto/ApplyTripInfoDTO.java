package com.fenbeitong.openapi.plugin.func.apply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApplyTripInfoDTO {

    private List<Integer> multi_trip_scene;

    private List<MultiTripCity> multi_trip_city;

    private List<MultiTripCityOutput> multitrip_cities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiTripCity {

        private String key;

        private String value;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiTripCityOutput {

        private String id;

        private String name;

    }

    private String id;

    private Integer type;

    private String start_time;

    private String end_time;

    private String start_city_id;
    private String start_city_name;
    /**
     * apply/detail接口返回城市的详细信息(vender城市信息)
     */
    private Object start_city;

    private String arrival_city_id;
    private String arrival_city_name;
    /**
     * apply/detail接口返回城市的详细信息(vender城市信息)
     */
    private Object arrival_city;

    private BigDecimal estimated_amount;

    private boolean avaliable;

    private String start_description;

    private String arrival_description;

    private List start_station_list;

    private List arrival_station_list;

    private List start_all_station_list;

    private List arrival_all_station_list;

    private String title;

    private String content;

    private String order_id;

    private String status_name;

    private String status;

    private String order_time;

    private String person_count;

    private String extra_content;

    private String time_range;

    private String trip_content;

    private String person_content;

    private List<Map<String, Object>> mall_list;

    private Map<String, Object> address_info;

    //费用归属
    private Map<String, Object> cost_attribution_name;

    //订单事由id
    private Integer order_reason_id;

    //订单事由
    private String order_reason;

    //采购价格结构
    private Map<String, Object> mall_price_structure;

    private String price_structure;

    //订单事由描述
    private String order_reason_desc;

    //采购自定义字段
    private List<Map<String, Object>> custom_remark;

    //返程开始时间
    private String back_start_time;

    //返程结束时间
    private String back_end_time;

    //飞机航空名称
    private String air_airline_name;

    //飞机航班号
    private String air_flight_no;

    //飞机舱位名称
    private String air_seat_msg;

    //返程飞机航空名称
    private String back_air_airline_name;

    //返程飞机航班号
    private String back_air_flight_no;

    //返程飞机舱位名称
    private String back_air_seat_msg;

    //折扣信息
    private Double air_discount;

    //酒店床型名称
    private String bed_type;

    //预订间数
    private Integer room_count;

    //行程类型 1单程 2往返
    private Integer trip_type;

    //商品数量
    private Integer product_count;

    private String trip_details;

    //分贝券有效时间
    private String valid_time;

    //券备注
    private String remark;

    //退改订单信息
    private Map<String, Object> trip_order_info;

    //审批用车规则id
    private List<String> start_city_ids;

    //单日限额
    private BigDecimal day_price_limit;

    //单次限额
    private BigDecimal price_limit;

    //规则内容
    private List<Map<String, String>> rule_info;

    //金额限制类型
    private Integer price_limit_flag;

    //次数限制类型
    private Integer times_limit_flag;

    //审批用车城市
    private List<String> start_city_name_list;

    //乘机人/乘车人/入住人
    private List<String> travel_partner;

    private String address_id;

    private String company_address_id;

    private String address_name;

    private BigDecimal address_lat;

    private BigDecimal address_lng;

    private Integer address_tag;

    //交易信息
    private List<Map<String, Object>> trade_information;

    private String unset_start_arrival_city;

    //分贝劵是否可以转让 1可以,0不可以
    private Integer canTransfer;

    //订单通知人id列表
    private List<String> notifier_ids;

    //订单通知人信息
    private List<Map<String, Object>> notifiers;

    //费用类型 1.总计 2.人均
    private Integer cost_type;

    //平均费用
    private BigDecimal average_cost;

    //配送时间
    private String delivery_time;

    //0.手动下单 1.自动下单
    private Integer automatic_order;

    private List<String> attachment_list;
    //剩余可用额度
    private BigDecimal useable_budget;

    private String estimated_left;

}

