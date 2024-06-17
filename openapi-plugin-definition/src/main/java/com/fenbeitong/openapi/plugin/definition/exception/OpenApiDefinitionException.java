package com.fenbeitong.openapi.plugin.definition.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

/**
 * openapi配置异常
 * Created by log.chang on 2019/12/13.
 */
public class OpenApiDefinitionException extends OpenApiPluginException {

    public OpenApiDefinitionException(int code, Object... args) {
        super(code, args);
    }

    public OpenApiDefinitionException(int code) {
        super(code);
    }

    public OpenApiDefinitionException() {
        super();
    }

}
