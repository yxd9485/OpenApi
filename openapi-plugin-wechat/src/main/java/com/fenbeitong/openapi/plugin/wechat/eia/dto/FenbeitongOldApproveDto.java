package com.fenbeitong.openapi.plugin.wechat.eia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 分贝通差旅审批信息
 * @Auther zhang.peng
 * @Date 2021/4/30
 */
@Data
public class FenbeitongOldApproveDto {

    @JsonProperty(value = "company_id")
    private String companyId;

    @JsonProperty(value = "cost_attribution_name")
    private String costAttributionName;

    @JsonProperty(value = "third_employee_id")
    private String thirdEmployeeId;

    @JsonProperty(value = "apply_reason")
    private String applyReason;        //事由

    @JsonProperty(value = "apply_reason_desc")
    private String applyReasonDesc;   //事由描述

    @JsonProperty(value = "employee_name")
    private String employeeName;

    @JsonProperty(value = "apply_id")
    private String applyId;

    @JsonProperty(value = "apply_name")
    private String applyName;

    private List<Trip> tripList;

    @Data
    public static class Trip{

        @JsonProperty(value = "arrival_city_id")
        private String arrivalCityId;

        @JsonProperty(value = "arrival_city_name")
        private String arrivalCityName;

        @JsonProperty(value = "estimated_amount")
        private String estimatedAmount;

        @JsonProperty(value = "start_city_id")
        private String startCityId;

        @JsonProperty(value = "start_city_name")
        private String startCityName;

        @JsonProperty(value = "start_time")
        private String startTime;

        @JsonProperty(value = "end_time")
        private String endTime;

        private int type;    // 7 飞机 15 火车 11 酒店 3 用车 40 国际机票 ApplyTripType

        @JsonProperty(value = "start_description")
        private String startDescription;

        @JsonProperty(value = "rule_info")
        private List<RuleInfo> ruleInfos;

        @JsonProperty(value = "start_city_name_list")
        private List<String> startCityNameList;

        @JsonProperty(value = "start_city_ids")
        private List<String> startCityIds;

        @JsonProperty(value = "mall_list")
        private List<Mall> mallList;

        @JsonProperty(value = "order_reason")
        private String orderReason;

        @JsonProperty(value = "order_reason_desc")
        private String orderReasonDesc;

    }

    @Data
    public static class RuleInfo{

        private String key;

        private String value;
    }

    @Data
    public static class Mall{

        private String amount;

        private String checked;

        private String city;

        private String count;

        private String county;

        private Gift gift;

        private String id;

        @JsonProperty(value = "image_url")
        private String imageUrl;

        @JsonProperty(value = "is_seven_days_return")
        private String isSevenDaysReturn;

        @JsonProperty(value = "isfb_product")
        private String isfbProduct;

        private String key;

        @JsonProperty(value = "limit_usefbq_flag")
        private String limitUsefbqFlag;

        private String name;

        @JsonProperty("order_type")
        private String orderType;

        private String pic;

        private String price;

        private String province;

        private String remain;

        @JsonProperty(value = "sale_price")
        private String salePrice;

        @JsonProperty(value = "sku_id")
        private String skuId;

        private String status;

        @JsonProperty(value = "stock_state_id")
        private String stockStateId;

        private String value;

        @JsonProperty(value = "vendor_id")
        private String vendorId;
    }

    @Data
    public static class Gift{

        private List<String> accessorys;

        private List<String> gifts;

        @JsonProperty(value = "max_num")
        private int maxNum;

        @JsonProperty(value = "min_num")
        private int minNum;

        @JsonProperty(value = "promo_end_time")
        private int promoEndTime;

        @JsonProperty(value = "promo_start_time")
        private int promoStartTime;
    }
}
