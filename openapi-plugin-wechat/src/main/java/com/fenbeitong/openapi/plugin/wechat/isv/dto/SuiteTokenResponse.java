package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取suiteToken返回
 * Created by lizhen on 2020/3/19.
 */
@Data
public class SuiteTokenResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("suite_access_token")
    private String suiteAccessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

}
