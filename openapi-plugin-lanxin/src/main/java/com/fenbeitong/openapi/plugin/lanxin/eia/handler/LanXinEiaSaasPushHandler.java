package com.fenbeitong.openapi.plugin.lanxin.eia.handler;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
import com.fenbeitong.openapi.plugin.lanxin.eia.service.LanXInEiaMessageService;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LanXinEiaSaasPushHandler extends EventHandler<SaasPushEvents> {

   @Autowired
    private LanXInEiaMessageService lanXInEiaMessageService;


    @WebAppEvent(type = "saas", version = "1.0", value = 20)
    @Override
    public boolean process(SaasPushEvents saasPushEvents, Object... args) {
        lanXInEiaMessageService.pushMessage(saasPushEvents);
        return true;
    }
}
