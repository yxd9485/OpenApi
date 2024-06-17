package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author lizhen
 * @date 2020/6/1
 */
@Data
public class FeiShuTenantAccessTokenReqDTO {

    @JsonProperty("app_access_token")
    private String appAccessToken;

    @JsonProperty("tenant_key")
    private String tenantKey;

}
