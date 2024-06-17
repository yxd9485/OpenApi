package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 分贝通差旅审批信息
 * @Auther zhang.peng
 * @Date 2021/4/30
 */
@Data
public class FenbeitongTripApproveDto {

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

    @JsonProperty(value = "trip_list")
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

    }

    @Data
    public static class RuleInfo{

        private String key;

        private String value;
    }
}
