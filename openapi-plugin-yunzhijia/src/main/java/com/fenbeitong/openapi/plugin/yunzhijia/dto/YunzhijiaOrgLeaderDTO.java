package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaOrgLeaderDTO {

    @JsonProperty("openId")
    private String openId;

    @JsonProperty("departmentId")
    private String departmentId;

    @JsonProperty("department")
    private String department;


}
