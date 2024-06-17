package com.fenbeitong.openapi.plugin.customize.qiqi.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 *
 * @author helu
 * @date 2022/5/14 上午10:42
 */
public class OpenApiQiqiException extends OpenApiPluginException {

    public OpenApiQiqiException(int code) {
        super(code);
    }

    public OpenApiQiqiException(int code, Object... args) {
        super(code, args);
    }
}
