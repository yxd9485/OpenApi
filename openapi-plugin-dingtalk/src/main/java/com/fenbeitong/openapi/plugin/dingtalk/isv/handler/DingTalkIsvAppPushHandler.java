package com.fenbeitong.openapi.plugin.dingtalk.isv.handler;

import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvMessageService;
import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
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
public class DingTalkIsvAppPushHandler extends EventHandler<AppPushEvents> {

    @Autowired
    private IDingtalkIsvMessageService dingtalkIsvMessageService;


    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;


    @WebAppEvent(type = "app", version = "1.0", value = 11)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        dingtalkIsvMessageService.pushMessage(appPushEvents);
        return true;
    }
}
