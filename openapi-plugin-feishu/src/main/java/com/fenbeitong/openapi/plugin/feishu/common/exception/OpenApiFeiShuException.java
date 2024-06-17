package com.fenbeitong.openapi.plugin.feishu.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * <p>Title: OpenApiWechatException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 5:03 PM
 */
public class OpenApiFeiShuException extends OpenApiPluginException {

    public OpenApiFeiShuException(int code) {
        super(code);
    }

    public OpenApiFeiShuException(int code, Object... args) {
        super(code, args);
    }
}
