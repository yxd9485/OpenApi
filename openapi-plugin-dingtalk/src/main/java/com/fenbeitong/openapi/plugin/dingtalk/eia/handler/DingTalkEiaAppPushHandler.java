package com.fenbeitong.openapi.plugin.dingtalk.eia.handler;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaMessageService;
import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DingTalkEiaAppPushHandler extends EventHandler<AppPushEvents> {

    @Autowired
    private IDingtalkEiaMessageService dingtalkEiaMessageService;


    @WebAppEvent(type = "app", version = "1.0", value = 1)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        dingtalkEiaMessageService.pushMessage(appPushEvents);
        return true;
    }
}
