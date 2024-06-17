package com.fenbeitong.openapi.plugin.fxiaoke.sdk.handler;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkMessageService;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/12/28.
 */
@Component
public class FxiaokeSaasPushHandler extends EventHandler<SaasPushEvents> {

    @Autowired
    private IFxkMessageService fxkMessageService;

    @WebAppEvent(type = "saas", version = "1.0", value = 12)
    @Override
    public boolean process(SaasPushEvents saasPushEvents, Object... args) {
        fxkMessageService.pushMessage(saasPushEvents);
        return true;
    }
}
