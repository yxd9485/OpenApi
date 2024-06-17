package com.fenbeitong.openapi.plugin.ecology.v8.common;

import org.slf4j.MDC;

/**
 * <p>Title: EcologyResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
public class EcologyResponseUtils {

    public static EcologyResultEntity success(Object data) {
        EcologyResultEntity result = new EcologyResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static EcologyResultEntity error(int code, String msg) {
        EcologyResultEntity result = new EcologyResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
