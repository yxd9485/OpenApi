package com.fenbeitong.openapi.plugin.dingtalk.common;

import org.slf4j.MDC;

/**
 * @author xiaohai
 */
public class DingtalkApproveKitResponseUtils {

    public static DingtalkApproveKitResultEntity success(Object data) {
        DingtalkApproveKitResultEntity result = new DingtalkApproveKitResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setSuccess(true);
        result.setErrorCode("0");
        result.setErrorMessage("success");
        result.setData(data);
        return result;
    }

    public static DingtalkApproveKitResultEntity error(String msg) {
        DingtalkApproveKitResultEntity result = new DingtalkApproveKitResultEntity();
        result.setRequestId(MDC.get("requestId"));
        result.setSuccess(false);
        result.setErrorCode("-99999");
        result.setErrorMessage(msg);
        return result;
    }
}
