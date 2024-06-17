package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


/**
 * 功能簇子功能
 * Created by lizhen on 2020/01/13.
 */
@Data
@Builder
public class OpenGroupFunctionDTO {
    /**
     * 功能簇编码
     */
    @JsonProperty("group_code")
    private String groupCode;
    /**
     * 功能编码
     */
    @JsonProperty("function_code")
    private String functionCode;
    /**
     * 功能簇子功能名称，如钉钉审批
     */
    @JsonProperty("group_function_name")
    private String groupFunctionName;
    /**
     * 功能簇子功能编码
     */
    @JsonProperty("group_function_code")
    private String groupFunctionCode;
    /**
     * 功能状态1:启用，2:禁用
     */
    @JsonProperty("status")
    private Integer status;
}
