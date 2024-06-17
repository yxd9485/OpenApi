package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-23 14:17:36
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuIsvNotifyReqDTO {

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("notify_content")
    private String notifyContent;

    @JsonProperty("pc_schema")
    private Schema pcSchema;

    @JsonProperty("ios_schema")
    private Schema iosSchema;

    @JsonProperty("android_schema")
    private Schema androidSchema;

    @Data
    public static class Schema {

        private String path;

        private String query;
    }

}