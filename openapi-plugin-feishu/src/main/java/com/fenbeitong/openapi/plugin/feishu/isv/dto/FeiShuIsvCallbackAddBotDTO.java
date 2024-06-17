package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * create on 2020-09-18 17:37:33
 * @author lizhen
 */
@Data
public class FeiShuIsvCallbackAddBotDTO {

    private String ts;

    private String uuid;

    private String token;

    private String type;

    private Event event;

    @Data
    public static class Event {

        @JsonProperty("app_id")
        private String appId;

        @JsonProperty("chat_i18n_names")
        private ChatI18nNames chatI18nNames;

        @JsonProperty("chat_name")
        private String chatName;

        @JsonProperty("chat_owner_employee_id")
        private String chatOwnerEmployeeId;

        @JsonProperty("chat_owner_name")
        private String chatOwnerName;

        @JsonProperty("chat_owner_open_id")
        private String chatOwnerOpenId;

        @JsonProperty("open_chat_id")
        private String openChatId;

        @JsonProperty("operator_employee_id")
        private String operatorEmployeeId;

        @JsonProperty("operator_name")
        private String operatorName;

        @JsonProperty("operator_open_id")
        private String operatorOpenId;

        @JsonProperty("owner_is_bot")
        private boolean ownerIsBot;

        @JsonProperty("tenant_key")
        private String tenantKey;

        private String type;

    }

    @Data
    public static class ChatI18nNames {

        @JsonProperty("en_us")
        private String enUs;

        @JsonProperty("zh_cn")
        private String zhCn;

    }
}