package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;

/**
 * 云之家发送消息
 *
 * @Auther zhang.peng
 * @Date 2021/7/28
 */
public interface IYunzhijiaMessageService {

    /**
     * 向云之家发送公共号消息
     *
     * @param saasPushEvents app推送的消息
     */
    void pushMessageByPublicModel(SaasPushEvents saasPushEvents);
}
