package com.fenbeitong.openapi.plugin.dingtalk.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DingtalkApproveKitResultEntity<T> {

    @JsonProperty("request_id")
    private String requestId;

    private Boolean success;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    private T data;
}
