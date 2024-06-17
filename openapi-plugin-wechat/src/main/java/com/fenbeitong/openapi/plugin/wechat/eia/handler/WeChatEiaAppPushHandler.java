package com.fenbeitong.openapi.plugin.wechat.eia.handler;

import com.fenbeitong.openapi.plugin.event.app.dto.AppPushEvents;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.wechat.common.notice.sender.WeChatNoticeSender;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvMessageService;
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
public class WeChatEiaAppPushHandler extends EventHandler<AppPushEvents> {

    @Autowired
    private WeChatIsvMessageService weChatIsvMessageService;

    @Autowired
    private WeChatNoticeSender weChatNoticeSender;


    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @WebAppEvent(type = "app", version = "1.0", value = 2)
    @Override
    public boolean process(AppPushEvents appPushEvents, Object... args) {
        weChatNoticeSender.pushMessage(appPushEvents);
        return true;
    }
}
