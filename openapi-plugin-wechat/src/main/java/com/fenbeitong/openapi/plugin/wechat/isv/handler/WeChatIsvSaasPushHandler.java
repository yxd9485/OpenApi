package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
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
public class WeChatIsvSaasPushHandler extends EventHandler<SaasPushEvents> {

    @Autowired
    private WeChatIsvMessageService weChatIsvMessageService;

    @Autowired
    private WeChatNoticeSender weChatNoticeSender;


    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @WebAppEvent(type = "saas", version = "1.0", value = 3)
    @Override
    public boolean process(SaasPushEvents saasPushEvents, Object... args) {
        weChatIsvMessageService.pushMessage(saasPushEvents);
        return true;
    }
}
