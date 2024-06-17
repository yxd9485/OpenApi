package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 北森的审批单数据结构
 *
 * @author xiaowei
 * @date 2020/07/29
 */
@Data
public class BeisenApplyListDTO {

    @JsonProperty("Data")
    private ResultData data;
    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;


    @Data
    public static class ResultData {
        @JsonProperty("BusinessList")
        private List<BusinessList> businessList;
        @JsonProperty("Total")
        private int total;

    }

    @Data
    public static class BusinessList {

        @JsonProperty("StaffId")
        private String staffId;
        @JsonProperty("CardNumber")
        private String cardNumber;
        @JsonProperty("ApplyUser")
        private String applyUser;
        @JsonProperty("DepartmentId")
        private String departmentId;
        @JsonProperty("Reason")
        private String reason;
        @JsonProperty("DocumentType")
        private String documentType;
        @JsonProperty("SerialNumber")
        private String serialNumber;
        @JsonProperty("BusinessDetailsSync")
        private List<BusinessDetailsSync> businessDetailsSync;
        @JsonProperty("ObjectId")
        private String objectId;
        @JsonProperty("OId")
        private String oId;
        @JsonProperty("ParentId")
        private String parentId;
        @JsonProperty("StdOrganization")
        private String stdOrganization;
        @JsonProperty("StaffEmail")
        private String staffEmail;
        @JsonProperty("StartDateTime")
        private Date startDateTime;
        @JsonProperty("StopDateTime")
        private Date stopDateTime;
        @JsonProperty("ApplyTime")
        private String applyTime;
        @JsonProperty("ApproveStatus")
        private String approveStatus;
        @JsonProperty("JobNumber")
        private String jobNumber;

    }

    @Data
    public static class BusinessDetailsSync {

        @JsonProperty("StaffId")
        private String staffId;
        @JsonProperty("CardNumber")
        private String cardNumber;
        @JsonProperty("Address")
        private String address;
        @JsonProperty("StaffEmail")
        private String staffEmail;
        @JsonProperty("DocumentType")
        private String documentType;
        @JsonProperty("BusinessVehicle")
        private String businessVehicle;
        @JsonProperty("DeparturePlace")
        private String departurePlace;
        @JsonProperty("Destination")
        private String destination;
        @JsonProperty("ApproveStatus")
        private String approveStatus;
        @JsonProperty("Remark")
        private String remark;
        @JsonProperty("StartDateTime")
        private Date startDateTime;
        @JsonProperty("StopDateTime")
        private Date stopDateTime;
        private String tripType;

    }



}
