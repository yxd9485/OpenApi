package com.fenbeitong.openapi.plugin.landray.ekp.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finhub.framework.web.vo.BaseResult;
import lombok.Data;


@Data
public class LandaryEkpResultEntity<T> extends BaseResult {

    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private String msg;

    private T data;
}
