package com.fenbeitong.openapi.plugin.dingtalk.common;

import org.slf4j.MDC;

/**
 * <p>Title: DingtalkResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
public class DingtalkResponseUtils {

    public static DingtalkResultEntity success(Object data) {
        DingtalkResultEntity result = new DingtalkResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static DingtalkResultEntity error(int code, String msg) {
        DingtalkResultEntity result = new DingtalkResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
