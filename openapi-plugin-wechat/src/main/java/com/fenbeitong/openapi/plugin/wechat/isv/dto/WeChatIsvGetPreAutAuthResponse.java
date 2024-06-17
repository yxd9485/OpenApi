package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 获取预授权码
 *
 * @author lizhen
 * @date 2020/9/15
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvGetPreAutAuthResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("pre_auth_code")
    private String preAuthCode;

    @JsonProperty("expires_in")
    private Integer expiresIn;
}
