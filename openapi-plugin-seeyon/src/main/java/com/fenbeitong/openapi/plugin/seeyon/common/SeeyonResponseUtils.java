package com.fenbeitong.openapi.plugin.seeyon.common;

import org.slf4j.MDC;

public class SeeyonResponseUtils {
    public static SeeyonResultEntity success(Object data) {
        SeeyonResultEntity result = new SeeyonResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static SeeyonResultEntity error(int code, String msg) {
        SeeyonResultEntity result = new SeeyonResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
