package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvMessageService;
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
public class FeiShuIsvAppPushHandler extends EventHandler<AppPushEvents> {

    @Autowired
    private FeiShuIsvMessageService feiShuIsvMessageService;


    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @WebAppEvent(type = "app", version = "1.0", value = 8)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        feiShuIsvMessageService.pushMessage(appPushEvents);
        return true;
    }
}
