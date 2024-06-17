package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * create on 2020-09-18 17:37:33
 * @author lizhen
 */
@Data
public class FeiShuIsvCallbackAddUserToChatDTO {

    private String ts;

    private String uuid;

    private String token;

    private String type;

    private Event event;

    @Data
    public static class Event {

        @JsonProperty("app_id")
        private String appId;

        @JsonProperty("chat_id")
        private String chatId;

        private Operator operator;

        @JsonProperty("tenant_key")
        private String tenantKey;

        private String type;

        private List<User> users;
    }

    @Data
    public static class Operator {

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("user_id")
        private String userId;

    }

    @Data
    public static class User {

        private String name;

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("user_id")
        private String userId;

    }
}