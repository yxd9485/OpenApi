package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaResponse<T> implements Serializable {
    @JsonProperty("errorCode")
    private int errorCode;
    @JsonProperty("error")
    private String error;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("data")
    private T data;
}
