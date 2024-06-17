package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BeisenEmployeeOrganizationDTO
 * @Description 员工任职机构
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/16
 **/
@Data
public class BeisenEmployeeOrganizationDTO {
    @JsonProperty("scrollId")
    private String scrollId;
    @JsonProperty("isLastData")
    private boolean isLastData;
    @JsonProperty("total")
    private int total;
    @JsonProperty("data")
    private List<BeisenEmployeeOrganizationInfo> data;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;

    @Data
    public static class BeisenEmployeeOrganizationInfo {
        @JsonProperty("employeeInfo")
        BeisenEmployeeInfo employeeInfo;
        @JsonProperty("recordInfo")
        BeisenRecordInfo recordInfo;

        @Data
        public static class BeisenEmployeeInfo {
            @JsonProperty("userID")
            private String userID;
            @JsonProperty("name")
            private String name;
        }
        @Data
        public static class BeisenRecordInfo {
            @JsonProperty("oIdOrganization")
            private String oIdOrganization;
        }
    }
}
