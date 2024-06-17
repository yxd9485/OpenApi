package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取app_access_token
 * @author lizhen
 * @date 2020/6/1
 */
@Data
public class FeiShuAppAccessTokenReqDTO {

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("app_secret")
    private String appSecret;

    @JsonProperty("app_ticket")
    private String appTicket;


}
