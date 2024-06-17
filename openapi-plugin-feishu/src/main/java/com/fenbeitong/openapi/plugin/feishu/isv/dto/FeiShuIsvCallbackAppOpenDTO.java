package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-06-01 15:43:47
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuIsvCallbackAppOpenDTO {

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

        private List<Applicants> applicants;

        private Installer installer;

    }

    @Data
    public static class Installer {
        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("user_id")
        private String userId;

    }

    @Data
    public static class Applicants {

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("user_id")
        private String userId;

    }
}