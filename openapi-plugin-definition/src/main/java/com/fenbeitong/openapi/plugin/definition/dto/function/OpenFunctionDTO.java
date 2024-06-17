package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


/**
 * 功能
 * Created by lizhen on 2020/01/13.
 */
@Data
@Builder
public class OpenFunctionDTO {

    /**
     * 功能编码
     */
    @JsonProperty("function_code")
    private String functionCode;
    /**
     * 功能名称
     */
    @JsonProperty("function_name")
    private String functionName;
}
