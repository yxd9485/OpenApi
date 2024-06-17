package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

/**
 * @author lizhen
 */
public interface IDingtalkIsvCallbackService {

    /**
     * 回调处理
     * @param requestBody
     */
    void dispatch(String requestBody);
}
