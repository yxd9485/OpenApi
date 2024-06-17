package com.fenbeitong.openapi.plugin.lanxin.eia.handler;

import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.lanxin.eia.service.LanXInEiaMessageService;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LanXinEiaAppPushHandler extends EventHandler<AppPushEvents> {

    @Autowired
    private LanXInEiaMessageService lanXInEiaMessageService;

    @WebAppEvent(type = "app", version = "1.0", value = 20)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        lanXInEiaMessageService.pushMessage(appPushEvents);
        return true;
    }
}
