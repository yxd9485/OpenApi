package com.fenbeitong.openapi.plugin.customize.common.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;

public class OpenApiCustomizeException extends OpenApiPluginException {

    public OpenApiCustomizeException(int code, Object... args) {
        super(code, args);
    }

    public OpenApiCustomizeException(int code) {
        super(code);
    }

    public OpenApiCustomizeException() {
        super();
    }

}