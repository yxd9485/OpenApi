package com.fenbeitong.openapi.plugin.welink.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiWechatException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 5:03 PM
 */
public class OpenApiWeLinkException extends OpenApiPluginException {

    public OpenApiWeLinkException(int code) {
        super(code);
    }

    public OpenApiWeLinkException(int code, Object... args) {
        super(code, args);
    }
}
