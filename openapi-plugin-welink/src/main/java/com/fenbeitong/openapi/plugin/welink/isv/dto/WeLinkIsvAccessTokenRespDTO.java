package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-14 20:49:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvAccessTokenRespDTO {

    private String code;

    private String message;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

}