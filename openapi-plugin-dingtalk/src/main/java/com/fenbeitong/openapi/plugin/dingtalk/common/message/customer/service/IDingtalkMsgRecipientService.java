package com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.service;

import com.fenbeitong.openapi.plugin.support.common.constant.MsgEventType;

/**
 * Created by lizhen on 2020/11/16.
 */
public interface IDingtalkMsgRecipientService {

    /**
     * 获取企业消息接受人id
     * @param corpId
     * @param msgEventType
     * @return
     */
    String getMsgRecipientList(String corpId, MsgEventType msgEventType);

}
