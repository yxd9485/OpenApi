package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 设置授权配置请求参数
 *
 * @author log.chang
 * @date 2020/3/12
 */
@Data
public class WeChatIsvSetSessionInfoRequest {

    @JsonProperty("pre_auth_code")
    private String preAuthCode;

    @JsonProperty("session_info")
    private SessionInfo sessionInfo;


    @Data
    public static class SessionInfo {

        private List<Long> appid;

        @JsonProperty("auth_type")
        private Integer authType;


    }


}
