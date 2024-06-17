package com.fenbeitong.openapi.plugin.beisen.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiBeisenException<p>
 * <p>Description: 北森模块异常类<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/12 15:17
 */
public class OpenApiBeisenException extends OpenApiPluginException{
    public OpenApiBeisenException(int code) {
        super(code);
    }

    public OpenApiBeisenException(int code, Object... args) {
        super(code, args);
    }
}
