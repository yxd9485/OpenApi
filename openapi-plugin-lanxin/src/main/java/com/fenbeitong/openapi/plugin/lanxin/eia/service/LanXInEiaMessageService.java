package com.fenbeitong.openapi.plugin.lanxin.eia.service;

import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;


public interface LanXInEiaMessageService {
    /**
     * 向蓝信推送消息
     *
     * @return
     */
    void pushMessage(WebAppPushEvents kafkaPushMsg);
}
