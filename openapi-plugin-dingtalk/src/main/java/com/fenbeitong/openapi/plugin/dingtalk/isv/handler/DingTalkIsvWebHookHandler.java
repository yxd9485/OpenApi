package com.fenbeitong.openapi.plugin.dingtalk.isv.handler;

import com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl.DingtalkIsvWorkrecordService;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebHookOrderEvent;
import com.fenbeitong.openapi.plugin.support.annotation.WebHookEvent;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author xiaohai
 * @Date 2021-11-03
 **/
@Component
@Slf4j
public class DingTalkIsvWebHookHandler extends EventHandler<WebHookOrderEvent> {

    @Autowired
    private DingtalkIsvWorkrecordService dingtalkIsvWorkrecordService;

    @WebHookEvent( type = 11 )
    @Override
    public boolean process(WebHookOrderEvent webHookOrderEvent, Object... args) {
        log.info("接收待办数据：webHookOrderEvent：{}" , JsonUtils.toJson( webHookOrderEvent ));
        dingtalkIsvWorkrecordService.pushMessage(webHookOrderEvent);
        return true;
    }
}
