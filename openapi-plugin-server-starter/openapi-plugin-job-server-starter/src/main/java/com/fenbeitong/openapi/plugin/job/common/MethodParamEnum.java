package com.fenbeitong.openapi.plugin.job.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * method参数枚举
 * 仅支持get/post
 *
 * @author ctl
 * @date 2022/7/6
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum MethodParamEnum {

    GET("get", "get请求"),

    POST("post", "post请求");

    private String method;

    private String desc;

    /**
     * 是否为get请求
     *
     * @param method 请求参数
     * @return true是 false否
     */
    public static boolean isGet(String method) {
        return GET.getMethod().equalsIgnoreCase(method);
    }

    /**
     * 是否为post请求
     *
     * @param method 请求参数
     * @return true是 false否
     */
    public static boolean isPost(String method) {
        return POST.getMethod().equalsIgnoreCase(method);
    }

    /**
     * 是否为get/post请求
     *
     * @param method 请求参数
     * @return true是 false否
     */
    public static boolean verify(String method) {
        return Arrays.stream(values()).map(e -> e.getMethod()).collect(Collectors.toList()).contains(method);
    }

}
