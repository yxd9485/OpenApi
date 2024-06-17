package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaMessageService;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author duhui
 * @Date 2020-11-07
 **/
@Component
@Slf4j
@SuppressWarnings("all")
public class FeiShuEiaAppPushHandler extends EventHandler<AppPushEvents> {

    @Autowired
    private FeiShuEiaMessageService feiShuEiaMessageService;


    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @WebAppEvent(type = "app", version = "1.0", value = 10)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        feiShuEiaMessageService.pushMessage(appPushEvents);
        return true;
    }
}
