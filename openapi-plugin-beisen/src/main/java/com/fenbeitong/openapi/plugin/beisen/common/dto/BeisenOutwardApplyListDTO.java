package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 北森的公出单数据结构
 *
 * @author xiaowei
 * @date 2020/09/21
 */
@Data
public class BeisenOutwardApplyListDTO {

    @JsonProperty("Data")
    private ResultData data;
    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;


    @Data
    public static class ResultData {
        @JsonProperty("OutwardInfos")
        private List<OutwardInfo> outwardInfos;
        @JsonProperty("Total")
        private int total;

    }

    @Data
    public static class OutwardInfo {
        @JsonProperty("StaffId")
        private String staffId;
        @JsonProperty("StdOrganization")
        private String stdOrganization;
        @JsonProperty("DurationDisplay")
        private String durationDisplay;
        @JsonProperty("OutwardActualDurationIncludeUnit")
        private String outwardActualDurationIncludeUnit;
        @JsonProperty("OutwardReason")
        private String outwardReason;
        @JsonProperty("OutwardType")
        private String outwardType;
        @JsonProperty("UserID")
        private String userId;
        @JsonProperty("OId")
        private String oId;
        @JsonProperty("StaffEmail")
        private String staffEmail;
        @JsonProperty("OutwardStartDateTime")
        private Date outwardStartDateTime;
        @JsonProperty("OutwardStopDateTime")
        private Date outwardStopDateTime;
        @JsonProperty("ApplyTime")
        private String applyTime;
        @JsonProperty("ApproveStatus")
        private String approveStatus;
        @JsonProperty("Properties")
        private Map properties;
        private String destCityName;
        private String cityId;

    }


}
