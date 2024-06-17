package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

/**
 * @author lizhen
 */
public interface IDingtalkEiaCallbackService {
    void dispatch(String eventJson);
}
