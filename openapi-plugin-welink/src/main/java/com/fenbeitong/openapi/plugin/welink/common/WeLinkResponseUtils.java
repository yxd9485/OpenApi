package com.fenbeitong.openapi.plugin.welink.common;

import org.slf4j.MDC;

/**
 * <p>Title: WechatResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
public class WeLinkResponseUtils {

    public static WeLinkResultEntity success(Object data) {
        WeLinkResultEntity result = new WeLinkResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static WeLinkResultEntity error(int code, String msg) {
        WeLinkResultEntity result = new WeLinkResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
