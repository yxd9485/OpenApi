package com.fenbeitong.openapi.plugin.daoyiyun.dto;

import com.finhub.framework.web.vo.BaseResult;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunResultEntity<T> extends BaseResult {

    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private String msg;

    private T data;
}
