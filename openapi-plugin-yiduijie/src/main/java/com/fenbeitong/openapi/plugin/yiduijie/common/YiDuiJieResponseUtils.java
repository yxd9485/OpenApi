package com.fenbeitong.openapi.plugin.yiduijie.common;

import org.slf4j.MDC;

/**
 * <p>Title: YiDuiJieResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/03/10 15:08 PM
 */
public class YiDuiJieResponseUtils {

    public static YiDuiJieResultEntity success(Object data) {
        YiDuiJieResultEntity result = new YiDuiJieResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static YiDuiJieResultEntity error(int code, String msg) {
        YiDuiJieResultEntity result = new YiDuiJieResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
