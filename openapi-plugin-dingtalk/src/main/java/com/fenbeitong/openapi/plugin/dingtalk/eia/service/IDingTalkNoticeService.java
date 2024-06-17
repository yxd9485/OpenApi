package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.MessageRequest;

/**
 * <p>Title: IDingTalkNoticeService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 8:04 PM
 */
public interface IDingTalkNoticeService {

    /**
     * 发送钉钉消息通知
     *
     * @param corpId     钉钉企业id
     * @param msgContent 消息内容
     */
    void sendMsg(String corpId, String msgContent);


    /**
     * 根据数据id发送一次钉钉消息通知
     *
     * @param corpId          钉钉企业id
     * @param dingtalkUserIds 钉钉用户id
     * @param msgContent      消息内容
     * @param dataId          钉钉数据id
     */
    void sendOneMsg(String corpId, String dingtalkUserIds, String msgContent, String dataId);

    /**
     * 发送钉钉消息通知
     *
     * @param corpId          钉钉企业id
     * @param dingtalkUserIds 钉钉用户id
     * @param msgContent      消息内容
     */
    void sendMsg(String corpId, String dingtalkUserIds, String msgContent);

    /**
     * 发送钉钉消息
     *
     * @param messageRequest message请求
     * @param corpId         corpId
     * @param agentId        应用agentId
     */
    void sendMsg(MessageRequest messageRequest, String corpId, long agentId);

    /**
     * 发送钉钉消息给csm
     * @param corpId
     * @param msgContent
     */
    void sendMsgToCsm(String corpId, String msgContent);

    /**
     * 发送钉钉消息给客户
     * @param corpId
     * @param msgContent
     * @param dataId
     */
    void sendMsgToCustomerAdmin(String corpId, String msgContent, String dataId);
}
