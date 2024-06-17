package com.fenbeitong.openapi.plugin.yunzhijia.common;

import org.slf4j.MDC;

public class YunzhijiaResponseUtils {
    public static YunzhijiaResultEntity success(Object data) {
        YunzhijiaResultEntity result = new YunzhijiaResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static YunzhijiaResultEntity error(int code, String msg) {
        YunzhijiaResultEntity result = new YunzhijiaResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
