package com.fenbeitong.openapi.plugin.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * http请求响应体
 * Created by log.chang on 2019/12/6.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpBody {

    private int code;
    private String msg;
    private Object data;

    public static HttpBody success(Object data) {
        return new HttpBody(0, "", data);
    }

    public static HttpBody error(int code, String msg) {
        return new HttpBody(code, msg, null);
    }

}
