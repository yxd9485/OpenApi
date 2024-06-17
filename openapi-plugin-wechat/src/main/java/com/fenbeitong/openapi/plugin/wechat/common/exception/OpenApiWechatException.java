package com.fenbeitong.openapi.plugin.wechat.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiWechatException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 5:03 PM
 */
public class OpenApiWechatException extends OpenApiPluginException {

    public OpenApiWechatException(int code) {
        super(code);
    }

    public OpenApiWechatException(int code, Object... args) {
        super(code, args);
    }
}
