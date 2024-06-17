package com.fenbeitong.openapi.plugin.definition.dto.company.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.company.enums.AuthStatus;
import lombok.Builder;
import lombok.Data;

/**
 * 企业对接注册响应信息
 * Created by log.chang on 2019/12/13.
 */
@Data
@Builder
public class AuthDefinitionInfoDTO {

    /**
     * 授权id（企业id）
     */
    @JsonProperty("app_id")
    String appId;
    /**
     * 授权名称（企业名称）
     */
    @JsonProperty("app_name")
    String appName;
    /**
     * app_key
     */
    @JsonProperty("app_key")
    String appKey;
    /**
     * sign_key
     */
    @JsonProperty("sign_key")
    String signKey;
    /**
     * 授权状态
     *
     * @see AuthStatus
     */
    int status;

}
