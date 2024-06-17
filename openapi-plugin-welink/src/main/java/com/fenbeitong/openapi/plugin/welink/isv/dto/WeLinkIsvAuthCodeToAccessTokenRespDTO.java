package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-15 16:47:8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvAuthCodeToAccessTokenRespDTO {

    @JsonProperty("access_token")
    private String accessToken;

    private String code;

    private String state;

    private String message;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private String expiresIn;

}