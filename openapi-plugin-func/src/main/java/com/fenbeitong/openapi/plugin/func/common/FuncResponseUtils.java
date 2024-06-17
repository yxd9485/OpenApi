package com.fenbeitong.openapi.plugin.func.common;

import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;

/**
 * <p>Title: FuncResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
public class FuncResponseUtils {

    public static FuncResultEntity success(Object data) {
        FuncResultEntity result = new FuncResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static FuncResultEntity error(int code, String msg) {
        FuncResultEntity result = new FuncResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static FuncResultEntity error(int code, String msg, Object data) {
        FuncResultEntity result = new FuncResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
