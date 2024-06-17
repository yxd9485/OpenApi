package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dave.hansins on 19/12/14.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatToken {

    /**
     * 返回信心
     */
    @JsonProperty("errmsg")
    private String errMsg;
    /**
     * 返回code标识
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 返回的access_token
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * token有效时间
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;
}
