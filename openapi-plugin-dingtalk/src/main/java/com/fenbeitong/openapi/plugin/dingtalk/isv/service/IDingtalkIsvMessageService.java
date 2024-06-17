package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;

/**
 *
 * @author lizhen
 * @date 2020/7/22
 */
public interface IDingtalkIsvMessageService {
    /**
     * 向钉钉推送消息
     *
     * @return
     */
    void pushMessage(WebAppPushEvents kafkaPushMsg);
}
