package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiWechatException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 5:03 PMs
 */
public class OpenApiFxkException extends OpenApiPluginException {

    public OpenApiFxkException(int code) {
        super(code);
    }

    public OpenApiFxkException(int code, Object... args) {
        super(code, args);
    }
}
