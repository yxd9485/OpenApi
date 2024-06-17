package com.fenbeitong.openapi.plugin.job.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * contentType参数枚举
 * 仅支持application/x-www-form-urlencoded 和 application/json
 *
 * @author ctl
 * @date 2022/7/6
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("all")
public enum ContentTypeParamEnum {

    FORM("application/x-www-form-urlencoded", "form传参"),

    JSON("application/json", "json传参");

    private String type;

    private String desc;

    /**
     * 是否为form传参
     *
     * @param type 请求类型
     * @return true是 false否
     */
    public static boolean isForm(String type) {
        return FORM.getType().equalsIgnoreCase(type);
    }

    /**
     * 是否为json传参
     *
     * @param type 请求类型
     * @return true是 false否
     */
    public static boolean isJson(String type) {
        return JSON.getType().equalsIgnoreCase(type);
    }

    /**
     * 是否为get/post请求
     *
     * @param type 请求类型
     * @return true是 false否
     */
    public static boolean verify(String type) {
        return Arrays.stream(values()).map(e -> e.getType()).collect(Collectors.toList()).contains(type);
    }

}
