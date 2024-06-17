package com.fenbeitong.openapi.plugin.customize.qiqi.common;

import org.slf4j.MDC;

/**
 *
 * @author helu
 * @date 2022/5/14 上午10:36
 */
public class QiqiResponseUtils {

    public static QiqiResultEntity success(Object data) {
        QiqiResultEntity result = new QiqiResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static QiqiResultEntity error(int code, String msg) {
        QiqiResultEntity result = new QiqiResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
