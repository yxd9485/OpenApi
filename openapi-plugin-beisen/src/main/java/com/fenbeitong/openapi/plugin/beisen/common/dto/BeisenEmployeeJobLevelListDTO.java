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
public class BeisenEmployeeJobLevelListDTO {

    @JsonProperty("_id")
    private String jobLevelId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("CreatedTime")
    private String createdTime;
    @JsonProperty("ModifiedTime")
    private String modifiedTime;
}
