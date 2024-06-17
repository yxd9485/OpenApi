package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YunzhijiaEmployeeContactDTO {

    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("publicid")
    private String publicid;
    @JsonProperty("permission")
    private String permission;
    @JsonProperty("value")
    private String value;

}
