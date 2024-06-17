package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 功能簇子功能前置条件
 * Created by lizhen on 2020/01/14.
 */
@Data
public class OpenGroupFunctionRelReqDTO {
    /**
     * 功能簇子功能编码
     */
    @NotBlank(message = "功能簇子功能编码[group_function_code]不可为空")
    @JsonProperty("group_function_code")
    private String groupFunctionCode;
    /**
     * 前置子功能编码
     */
    @NotNull(message = "前置子功能编码[pre_group_function_code]不可为空")
    @Size(min = 1, message = "前置子功能编码[pre_group_function_code]不可为空")
    @JsonProperty("pre_group_function_code")
    private List<String> preGroupFunctionCode;
}
