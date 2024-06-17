package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common;

import org.slf4j.MDC;

public class FxkResponseUtils {
    public static FxkResultEntity success(Object data) {
        FxkResultEntity result = new FxkResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static FxkResultEntity error(int code, String msg) {
        FxkResultEntity result = new FxkResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
