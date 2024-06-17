package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * create on 2020-09-18 17:37:33
 * @author lizhen
 */
@Data
public class FeiShuIsvCallbackBotMessageDTO {

    private String ts;

    private String uuid;

    private String token;

    private String type;

    private Event event;

    @Data
    public static class Event {

        private String type;

        @JsonProperty("app_id")
        private String appId;

        @JsonProperty("tenant_key")
        private String tenantKey;

        @JsonProperty("root_id")
        private String rootId;

        @JsonProperty("parent_id")
        private String parentId;

        @JsonProperty("open_chat_id")
        private String openChatId;

        @JsonProperty("chat_type")
        private String chatType;

        @JsonProperty("msg_type")
        private String msgType;

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("open_message_id")
        private String openMessageId;

        @JsonProperty("is_mention")
        private boolean isMention;

        private String text;

        @JsonProperty("text_without_at_bot")
        private String textWithoutAtBot;
    }

}