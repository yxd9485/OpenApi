package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 功能簇子功能
 * Created by lizhen on 2020/01/13.
 */
@Data
public class OpenGroupFunctionReqDTO {
    /**
     * 功能簇编码
     */
    @NotBlank(message = "功能簇编码[group_code]不可为空")
    @JsonProperty("group_code")
    private String groupCode;
    /**
     * 功能编码
     */
    @NotBlank(message = "功能编码[function_code]不可为空")
    @JsonProperty("function_code")
    private String functionCode;
}
