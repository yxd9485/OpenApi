package com.fenbeitong.openapi.plugin.daoyiyun.util;

import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunResultEntity;

import org.slf4j.MDC;

/**
 * @author lizhen
 */
public class DaoYiYunResponseUtils {


    public static DaoYiYunResultEntity success(Object data) {
        DaoYiYunResultEntity result = new DaoYiYunResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static DaoYiYunResultEntity error(int code, String msg) {
        DaoYiYunResultEntity result = new DaoYiYunResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
