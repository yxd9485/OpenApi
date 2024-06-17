package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeRespDTO;

/**
 * Created by lizhen on 2020/12/28.
 */
public interface IFxkMessageService {
    FxiaokeRespDTO pushMessage(WebAppPushEvents kafkaPushMsg);
}
