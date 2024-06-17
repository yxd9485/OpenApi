package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * open api 响应封装类
 * @author zhaokechun
 * @date 2018/11/21 14:30
 */
@Data
public class OpenApiResponse<T> implements Serializable {

    private String requestId;

    private int code;

    private String msg;

    private T data;
}
