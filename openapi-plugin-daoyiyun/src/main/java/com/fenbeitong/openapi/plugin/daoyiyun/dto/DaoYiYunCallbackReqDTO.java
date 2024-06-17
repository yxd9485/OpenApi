package com.fenbeitong.openapi.plugin.daoyiyun.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 道一云回调dto
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunCallbackReqDTO {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("eventBusinessId")
    private String eventBusinessId;

    private String data;
}
