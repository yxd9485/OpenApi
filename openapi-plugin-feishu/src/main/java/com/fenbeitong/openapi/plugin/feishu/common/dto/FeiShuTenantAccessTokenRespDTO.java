package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author lizhen
 * @date 2020/6/1
 */
@Data
public class FeiShuTenantAccessTokenRespDTO {

    private Integer code;

    private String msg;

    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;

    private Integer expire;

}
