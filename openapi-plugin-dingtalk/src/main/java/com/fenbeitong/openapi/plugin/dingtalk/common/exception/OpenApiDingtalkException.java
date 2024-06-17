package com.fenbeitong.openapi.plugin.dingtalk.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiDingtalkException</p>
 * <p>Description: 钉钉异常</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/15 2:59 PM
 */
public class OpenApiDingtalkException extends OpenApiPluginException {

    public OpenApiDingtalkException(int code, Object... args) {
        super(code, args);
    }

    public OpenApiDingtalkException(int code) {
        super(code);
    }

    public OpenApiDingtalkException() {
        super();
    }

}