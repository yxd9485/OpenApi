package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 功能簇
 * Created by lizhen on 2020/01/13.
 */
@Data
public class OpenGroupReqDTO {
    /**
     * 功能簇名称
     */
    @NotBlank(message = "功能簇名称[group_name]不可为空")
    @JsonProperty("group_name")
    private String groupName;
    /**
     * 功能簇编码
     */
    @NotBlank(message = "功能簇编码[group_code]不可为空")
    @JsonProperty("group_code")
    private String groupCode;
}
