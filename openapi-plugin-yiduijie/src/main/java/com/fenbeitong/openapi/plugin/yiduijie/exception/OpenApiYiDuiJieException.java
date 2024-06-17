package com.fenbeitong.openapi.plugin.yiduijie.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiYiDuiJieException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/10 5:03 PM
 */
public class OpenApiYiDuiJieException extends OpenApiPluginException {

    public OpenApiYiDuiJieException(int code) {
        super(code);
    }

    public OpenApiYiDuiJieException(int code, Object... args) {
        super(code, args);
    }
}
