package com.fenbeitong.openapi.plugin.yunzhijia.exception;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import lombok.Data;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
@Data
public class YunzhijiaException extends OpenApiPluginException {

    public YunzhijiaException(int code) {
        super(code);
    }

    public YunzhijiaException(int code, Object... args) {
        super(code, args);
    }
}
