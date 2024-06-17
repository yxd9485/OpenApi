package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


/**
 * 功能簇
 * Created by lizhen on 2020/01/13.
 */
@Data
@Builder
public class OpenGroupDTO {

    /**
     * 功能簇编码
     */
    @JsonProperty("group_code")
    private String groupCode;

    /**
     * 功能簇名称
     */
    @JsonProperty("group_name")
    private String groupName;
}
