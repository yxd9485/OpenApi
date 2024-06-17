package com.fenbeitong.openapi.plugin.definition.dto.company.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * 企业对接注册请求参数
 * Created by log.chang on 2019/12/13.
 */
@Data
@JsonNaming
@Builder
public class AuthRegisterReqDTO {

    /**
     * 授权id（企业id）
     */
    @JsonProperty("app_id")
    private String appId;
    /**
     * 授权名称（企业名称）
     */
    @JsonProperty("app_name")
    private String appName;
    /**
     * 是否使用虚拟号码，默认否
     */
    @JsonProperty("virtual_number")
    private Integer virtualNumber;
    /**
     * 授权配置备注
     */
    @JsonProperty("app_remark")
    private String appRemark;

}
