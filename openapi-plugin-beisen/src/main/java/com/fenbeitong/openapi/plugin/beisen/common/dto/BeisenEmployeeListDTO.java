package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 北森的组织数据结构
 *
 * @author xiaowei
 * @date 2020/06/16
 */
@Data
public class BeisenEmployeeListDTO {

    @JsonProperty("Total")
    private Integer total;
    @JsonProperty("Data")
    private List<EmployeeDto> data;
    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Extra")
    private String extra;


    @Data
    public static class EmployeeDto {
       @JsonProperty("BasicInfos")
       public EmployeeBasicInfos basicInfos;
       @JsonProperty("ServiceInfos")
       public List<EmployeeServiceInfos> serviceInfos;

    }
    @Data
    public static class EmployeeBasicInfos {
        @JsonProperty("UserID")
        private String userId;
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Email")
        private String email;
        @JsonProperty("MobilePhone")
        private String mobilePhone;
        @JsonProperty("IDNumber")
        private String idNumber;
        @JsonProperty("Gender")
        private Integer gender;
        @JsonProperty("StdIsDeleted")
        private Boolean stdIsDeleted;
        @JsonProperty("CreatedTime")
        private String createTime;
        @JsonProperty("ModifiedTime")
        private String modifiedTime;
        @JsonProperty("extend_1")
        private String extend_1;
        @JsonProperty("extend_2")
        private String extend_2;
        @JsonProperty("extend_3")
        private String extend_3;

    }

    @Data
    public static class EmployeeServiceInfos {
        @JsonProperty("UserID")
        private String userId;
        @JsonProperty("OIdDepartment")
        private String departmentId;
        @JsonProperty("StdIsDeleted")
        private String stdIsDeleted;
        /**
         * 1:待入职  2：使用 3：正式 4：调出  5：待调入  6：退休 8：离职  12：非正式
         */
        @JsonProperty("EmployeeStatus")
        private int employeeStatus;
        @JsonProperty("CreatedTime")
        private String createTime;
        @JsonProperty("ModifiedTime")
        private String modifiedTime;

    }

}
