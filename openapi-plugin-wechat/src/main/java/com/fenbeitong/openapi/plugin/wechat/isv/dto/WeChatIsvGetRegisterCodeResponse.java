package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-17 22:16:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvGetRegisterCodeResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("register_code")
    private String registerCode;

    @JsonProperty("expires_in")
    private Integer expiresIn;


}