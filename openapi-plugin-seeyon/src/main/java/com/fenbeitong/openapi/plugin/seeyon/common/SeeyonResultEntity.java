package com.fenbeitong.openapi.plugin.seeyon.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finhub.framework.web.vo.BaseResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonResultEntity<T> extends BaseResult {

    @JsonProperty("request_id")
    private String requestId;
    private Integer code;
    private String msg;
    private T data;
}
