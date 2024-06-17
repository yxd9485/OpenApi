package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * create on 2020-06-01 17:37:33
 */
@Data
public class FeiShuIsvCallbackAppTicketDTO {

    private String ts;

    private String uuid;

    private String token;

    private String type;

    private Event event;

    @Data
    public static class Event {

        @JsonProperty("app_id")
        private String appId;

        @JsonProperty("app_ticket")
        private String appTicket;

        private String type;

    }

}