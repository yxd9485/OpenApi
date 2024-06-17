package com.fenbeitong.openapi.plugin.dingtalk.eia.handler;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkEiaWorkrecordService;
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
public class DingTalkEiaWebHookHandler extends EventHandler<WebHookOrderEvent> {

    @Autowired
    private DingtalkEiaWorkrecordService dingtalkEiaWorkrecordService;

    @WebHookEvent( type = 1 )
    @Override
    public boolean process(WebHookOrderEvent webHookOrderEvent, Object... args) {
        log.info("接收待办数据：webHookOrderEvent：{}" , JsonUtils.toJson( webHookOrderEvent ));
        dingtalkEiaWorkrecordService.pushMessage(webHookOrderEvent);
        return true;
    }
}
