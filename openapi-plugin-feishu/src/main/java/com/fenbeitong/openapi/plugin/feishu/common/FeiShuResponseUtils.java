package com.fenbeitong.openapi.plugin.feishu.common;

import org.slf4j.MDC;

/**
 * <p>Title: WechatResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
public class FeiShuResponseUtils {

    public static FeiShuResultEntity success(Object data) {
        FeiShuResultEntity result = new FeiShuResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static FeiShuResultEntity error(int code, String msg) {
        FeiShuResultEntity result = new FeiShuResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
