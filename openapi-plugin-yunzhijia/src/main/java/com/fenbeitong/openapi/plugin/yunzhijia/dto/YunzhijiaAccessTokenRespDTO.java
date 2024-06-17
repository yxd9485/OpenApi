package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaAccessTokenRespDTO {

    /**
     * 返回的access_token
     */
//        @JsonProperty("access_token")
    private String accessToken;
    /**
     * token刷新令牌,刷新token时使用
     */
    private String refreshToken;
    /**
     * token有效时间
     */
//        @JsonProperty("expire_in")
    private Integer expireIn;
}
