package com.fenbeitong.openapi.plugin.lanxin.util;

import org.slf4j.MDC;

/**
 * <p>Title: LanXinResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 4:21 下午
 */
public class LanXinResponseUtils {
    public static LanXInResultEntity success(Object data) {
        LanXInResultEntity result = new LanXInResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static LanXInResultEntity error(int code, String msg) {
        LanXInResultEntity result = new LanXInResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
