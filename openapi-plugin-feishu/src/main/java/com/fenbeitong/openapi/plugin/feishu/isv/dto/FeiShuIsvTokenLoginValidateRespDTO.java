package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author lizhen
 * @date 2020/6/5
 */
@Data
public class FeiShuIsvTokenLoginValidateRespDTO {

    private Integer code;

    private String msg;

    private TokenLoginValidateData data;


    @Data
    public static class TokenLoginValidateData {

        private String uid;

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("union_id")
        private String unionId;

        @JsonProperty("session_key")
        private String sessionKey;

        @JsonProperty("tenant_key")
        private String tenantKey;

        @JsonProperty("employee_id")
        private String employeeId;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @JsonProperty("refresh_token")
        private String refreshToken;

    }
}

