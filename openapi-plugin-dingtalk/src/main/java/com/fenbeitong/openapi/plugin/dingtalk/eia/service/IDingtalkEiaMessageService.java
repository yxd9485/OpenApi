package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;


public interface IDingtalkEiaMessageService {
    /**
     * 向钉钉推送消息
     *
     * @return
     */
    void pushMessage(WebAppPushEvents kafkaPushMsg);
}
