package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 功能
 * Created by lizhen on 2020/01/13.
 */
@Data
public class OpenFunctionReqDTO {

    /**
     * 功能编码
     */
    @NotBlank(message = "功能编码[function_code]不可为空")
    @JsonProperty("function_code")
    private String functionCode;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名称[function_name]不可为空")
    @JsonProperty("function_name")
    private String functionName;

}
