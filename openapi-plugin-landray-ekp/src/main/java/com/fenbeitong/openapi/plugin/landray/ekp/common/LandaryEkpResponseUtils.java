package com.fenbeitong.openapi.plugin.landray.ekp.common;

import org.slf4j.MDC;


public class LandaryEkpResponseUtils {

    public static LandaryEkpResultEntity success(Object data) {
        LandaryEkpResultEntity result = new LandaryEkpResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static LandaryEkpResultEntity error(int code, String msg) {
        LandaryEkpResultEntity result = new LandaryEkpResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
