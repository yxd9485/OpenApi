package com.fenbeitong.openapi.plugin.wechat.common;

import org.slf4j.MDC;

/**
 * <p>Title: WechatResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
public class WechatResponseUtils {

    public static WechatResultEntity success(Object data) {
        WechatResultEntity result = new WechatResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static WechatResultEntity error(int code, String msg) {
        WechatResultEntity result = new WechatResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
