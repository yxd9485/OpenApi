package com.fenbeitong.openapi.plugin.dingtalk.isv.handler;

import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvMessageService;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
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
public class DingTalkIsvSaasPushHandler extends EventHandler<SaasPushEvents> {

    @Autowired
    private IDingtalkIsvMessageService dingtalkIsvMessageService;


    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @WebAppEvent(type = "saas", version = "1.0", value = 11)
    @Override
    public boolean process(SaasPushEvents saasPushEvents, Object... args) {
        dingtalkIsvMessageService.pushMessage(saasPushEvents);
        return true;
    }
}
