package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业后台授权登录用户信息请求参数
 * Created by log.chang on 2020/3/23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeChatIsvLoginInfoRequest {

    @JsonProperty("auth_code")
    private String authCode;

}
