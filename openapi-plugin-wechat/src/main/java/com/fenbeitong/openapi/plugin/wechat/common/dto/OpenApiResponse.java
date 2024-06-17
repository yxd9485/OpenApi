package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenApiResponse<T> implements Serializable {
    private String requestId;
    private int code;
    private String msg;
    private T data;
}
