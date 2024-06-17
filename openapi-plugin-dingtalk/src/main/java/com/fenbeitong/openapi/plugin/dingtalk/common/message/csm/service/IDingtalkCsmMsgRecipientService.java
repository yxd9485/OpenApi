package com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.service;

import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity.DingtalkCsmMsgRecipient;

/**
 * @author lizhen
 * @date 2020/11/12
 */
public interface IDingtalkCsmMsgRecipientService {

    /**
     * 根据企业ID查询企业和客户成功相关信息表数据
     *
     * @param corpId
     * @return
     */
    DingtalkCsmMsgRecipient getCsmMsgRecipient(String corpId);
}
