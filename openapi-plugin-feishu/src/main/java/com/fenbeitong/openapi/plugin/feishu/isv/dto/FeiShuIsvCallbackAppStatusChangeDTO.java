package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author lizhen
 */
@Data
public class FeiShuIsvCallbackAppStatusChangeDTO {

    private String ts;

    private String uuid;

    private String token;

    private String type;

    private Event event;

    @Data
    public static class Event {

        @JsonProperty("app_id")
        private String appId;

        @JsonProperty("tenant_key")
        private String tenantKey;

        private String type;

        private String status;

    }

}