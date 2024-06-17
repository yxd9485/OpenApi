package com.fenbeitong.openapi.plugin.func.exception;

import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;

/**
 * <p>Title: OpenApiFuncException</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/11 2:52 PM
 */
public class OpenApiFuncException extends OpenApiPluginSupportException {

    public OpenApiFuncException(int code, Object... args) {
        super(code, args);
    }

    public OpenApiFuncException(int code) {
        super(code);
    }

    public OpenApiFuncException() {
        super();
    }
}
