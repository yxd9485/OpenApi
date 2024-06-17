package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by lizhen on 2020/6/1.
 */
@Data
public class FeiShuCallbackBaseReqDTO {

    private String challenge;

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
    }
}
