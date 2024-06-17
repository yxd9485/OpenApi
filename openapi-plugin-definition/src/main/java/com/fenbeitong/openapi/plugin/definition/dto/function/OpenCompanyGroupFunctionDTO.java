package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


/**
 * 企业功能配置
 * Created by lizhen on 2020/01/13.
 */
@Data
@Builder
public class OpenCompanyGroupFunctionDTO {
    /**
     * 公司id
     */
    @JsonProperty("app_id")
    private String appId;
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
     * 功能状态1:启用，2:禁用
     */
    @JsonProperty("status")
    private Integer status;
}
