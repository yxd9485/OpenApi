package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2021-01-22 16:23:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuAuthenRespDTO {

    private Integer code;

    private String msg;

    private FeiShuAuthenData data;

    @Data
    public static class FeiShuAuthenData {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("avatar_thumb")
        private String avatarThumb;

        @JsonProperty("avatar_middle")
        private String avatarMiddle;

        @JsonProperty("avatar_big")
        private String avatarBig;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        private String name;

        @JsonProperty("en_name")
        private String enName;

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("union_id")
        private String unionId;

        private String email;

        @JsonProperty("user_id")
        private String userId;

        private String mobile;

        @JsonProperty("tenant_key")
        private String tenantKey;

        @JsonProperty("refresh_expires_in")
        private Integer refreshExpiresIn;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("token_type")
        private String tokenType;
    }
}