package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务商的token
 * Created by log.chang on 2020/3/23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeChatIsvProviderTokenResponse {

    private Integer errcode;
    private String errmsg;
    @JsonProperty("provider_access_token")
    private String providerAccessToken;
    @JsonProperty("expires_in")
    private Integer expiresIn;

}
