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
public class WeChatIsvProviderTokenRequest {

    @JsonProperty("corpid")
    private String corpId;
    @JsonProperty("provider_secret")
    private String providerSecret;

}
