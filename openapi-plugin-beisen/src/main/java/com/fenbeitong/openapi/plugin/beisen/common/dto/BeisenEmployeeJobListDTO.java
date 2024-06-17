package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 北森的组织数据结构
 *
 * @author xiaowei
 * @date 2020/06/16
 */
@Data
public class BeisenEmployeeJobListDTO {

    @JsonProperty("UserID")
    private String userId;
    @JsonProperty("EmployeeStatus")
    private String employeeStatus;
    @JsonProperty("OIdJobLevel")
    private String oldJobLevel;
    @JsonProperty("OIdDepartment")
    private String departmentId;
    @JsonProperty("CreatedTime")
    private String createdTime;
    @JsonProperty("ModifiedTime")
    private String modifiedTime;
}
