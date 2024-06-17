package com.fenbeitong.openapi.plugin.beisen.common.util;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenResultEntity;
import org.slf4j.MDC;

/**
 * <p>Title: BeisenResponseUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/08/03 4:54 PM
 */
public class BeisenResponseUtils {

    public static BeisenResultEntity success(Object data) {
        BeisenResultEntity result = new BeisenResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static BeisenResultEntity error(int code, String msg) {
        BeisenResultEntity result = new BeisenResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
