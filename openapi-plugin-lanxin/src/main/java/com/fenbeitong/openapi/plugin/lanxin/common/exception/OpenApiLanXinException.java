package com.fenbeitong.openapi.plugin.lanxin.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiDingtalkException</p>
 * <p>Description: 蓝信异常</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/04 2:59 PM
 */
public class OpenApiLanXinException extends OpenApiPluginException {

    public OpenApiLanXinException(int code, Object... args) {
        super(code, args);
    }

    public OpenApiLanXinException(int code) {
        super(code);
    }

    public OpenApiLanXinException() {
        super();
    }

}