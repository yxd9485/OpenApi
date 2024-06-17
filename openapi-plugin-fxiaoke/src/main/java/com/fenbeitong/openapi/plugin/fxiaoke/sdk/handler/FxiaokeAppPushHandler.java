package com.fenbeitong.openapi.plugin.fxiaoke.sdk.handler;

import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkMessageService;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/12/28.
 */
@Component
@Slf4j
public class FxiaokeAppPushHandler extends EventHandler<AppPushEvents> {
    @Autowired
    private IFxkMessageService fxkMessageService;

    @WebAppEvent(type = "app", version = "1.0", value = 12)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        fxkMessageService.pushMessage(appPushEvents);
        return true;
    }
}
