package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName BeisenEmpOrgDTO
 * @Description 北森员工任职机构
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/16
 **/
@Data
@Builder
public class BeisenEmpOrgDTO {
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("name")
    private String name;
    @JsonProperty("oIdOrganization")
    private String oIdOrganization;
}
