package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


/**
 * 功能簇子功能前置条件
 * Created by lizhen on 2020/01/14.
 */
@Data
@Builder
public class OpenGroupFunctionRelDTO {
    /**
     * 功能簇子功能编码
     */
    @JsonProperty("group_function_code")
    private String groupFunctionCode;
    /**
     * 功能簇子名称，如钉钉审批
     */
    @JsonProperty("group_function_name")
    private String groupFunctionName;
    /**
     * 前置子功能编码
     */
    @JsonProperty("pre_group_function_code")
    private String preGroupFunctionCode;
    /**
     * 前置子功能名称
     */
    @JsonProperty("pre_group_function_name")
    private String preGroupFunctionName;
}
