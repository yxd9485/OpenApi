package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * APP PUSH 消息到云之家
 * @Auther zhang.peng
 * @Date 2021/7/28
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class YunzhijiaSaaSPushHandler extends EventHandler<SaasPushEvents> {

    @Autowired
    private IYunzhijiaMessageService yunzhijiaMessageSendService;

    // openType = 4 云之家
    @WebAppEvent(type = "app", version = "1.0" , value = 4)
    @Override
    public boolean process(SaasPushEvents saasPushEvents, Object... args) {
        log.info("向云之家推送消息开始");
        yunzhijiaMessageSendService.pushMessageByPublicModel(saasPushEvents);
        return true;
    }
}
