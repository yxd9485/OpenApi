package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkMsgType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.MessageRequest;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.*;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.dao.MsgRecipientDefinitionDao;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.entity.MsgRecipientDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity.DingtalkCsmMsgRecipient;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.service.IDingtalkCsmMsgRecipientService;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.service.IDingtalkMsgRecipientService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.luastar.swift.base.utils.ObjUtils;
import com.luastar.swift.base.utils.StrUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>Title: DingTalkNoticeSender</p>
 * <p>Description: 钉钉消息通知服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 7:21 PM
 */
@Slf4j
@ServiceAspect
@Service
public class DingTalkNoticeServiceImpl implements IDingTalkNoticeService {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private IDingtalkCorpAppService dingtalkCorpAppService;

    @Autowired
    MsgRecipientDefinitionDao msgRecipientDefinitionDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IDingtalkCsmMsgRecipientService dingtalkCsmMsgRecipientService;

    @Autowired
    private IDingtalkMsgRecipientService dingtalkMsgRecipientService;

    @Override
    public void sendMsg(String corpId, String msgContent) {
        List<MsgRecipientDefinition> msgRecipientList = msgRecipientDefinitionDao.getMsgRecipientList(corpId, null);
        if (!ObjectUtils.isEmpty(msgRecipientList)) {
            List<String> userIdList = msgRecipientList.stream().map(MsgRecipientDefinition::getThirdUserId).collect(Collectors.toList());
            String userIds = String.join(",", userIdList);
            sendMsg(corpId, userIds, msgContent);
        }
    }

    @Override
    public void sendOneMsg(String corpId, String dingtalkUserIds, String msgContent, String dataId) {
        String redisKey = StrUtils.formatString(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, StrUtils.formatString(RedisKeyConstant.DINGTALK_SEND_MSG_KEY, dingtalkUserIds + "_" + dataId));
        String redisMessage = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotBlank(redisMessage)) {
            log.info("用户已推送过数据，不再推送，userId={}, dataId={}", dingtalkUserIds, dataId);
            return;
        } else {
            redisTemplate.opsForValue().set(redisKey, "1", 8, TimeUnit.DAYS);
        }
        sendMsg(corpId, dingtalkUserIds, msgContent);
    }

    @Override
    public void sendMsg(String corpId, String dingtalkUserIds, String msgContent) {
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCorpId(corpId);
        PluginCorpAppDefinition corpAppDefinition = dingtalkCorpAppService.getByCorpId(corpId);
        MessageRequest.Text content = new MessageRequest.Text();
        content.setContent(msgContent);
        MessageRequest msg = new MessageRequest();
        msg.setCompanyId(corpDefinition.getThirdCorpId());
        msg.setEmployeeIds(dingtalkUserIds);
        msg.setMsgType(DingtalkMsgType.TEXT);
        msg.setMsg(JsonUtils.toJson(content));
        sendMsg(msg, corpId, corpAppDefinition.getThirdAgentId());
    }

    /**
     * 发送钉钉消息
     *
     * @param messageRequest message请求
     * @param corpId         corpId
     * @param agentId        应用agentId
     */
    @Override
    public void sendMsg(MessageRequest messageRequest, String corpId, long agentId) {
        log.info("调用钉钉消息推送接口， 参数: messageRequest: {}, corpId: {}, agentId: {}", messageRequest, corpId, agentId);
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        String msgType = messageRequest.getMsgType();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(messageRequest.getEmployeeIds());
        request.setAgentId(agentId);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        switch (msgType) {
            case DingtalkMsgType.TEXT:
                MessageRequest.Text text = JsonUtils.toObj(messageRequest.getMsg(), MessageRequest.Text.class);
                msg.setMsgtype(messageRequest.getMsgType());
                msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
                msg.getText().setContent(text.getContent() + this.getMessageDate());
                break;
            case DingtalkMsgType.LINK:
                MessageRequest.Link link = JsonUtils.toObj(messageRequest.getMsg(), MessageRequest.Link.class);
                msg.setMsgtype("link");
                msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
                msg.getLink().setTitle(link.getTitle());
                msg.getLink().setText(link.getText() + this.getMessageDate());
                msg.getLink().setMessageUrl(link.getMessageUrl());
                msg.getLink().setPicUrl(link.getPicUrl());
                break;
            default:
                log.info("无法识别的的消息类型, messageType: {}", messageRequest.getMsgType());
                break;
        }
        request.setMsg(msg);
        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, accessToken);
            log.info("调用钉钉消息推送接口完成， 返回结果: {}", response.getBody());
            if (!response.isSuccess()) {
            }
        } catch (ApiException e) {
            log.error("调用钉钉消息推送接口异常", e);
        }
    }

    private String getMessageDate() {
        return "\r\n时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public void sendMsgToCsm(String corpId, String msgContent) {
        DingtalkCsmMsgRecipient csmMsgRecipient = dingtalkCsmMsgRecipientService.getCsmMsgRecipient(corpId);
        if (ObjUtils.isNotEmpty(csmMsgRecipient)) {
            String fbtCorpId = csmMsgRecipient.getFbtCorpId();
            String csmDingtalkId = csmMsgRecipient.getCsmDingtalkId();
            sendMsg(fbtCorpId, csmDingtalkId, msgContent);
        }
    }

    @Override
    public void sendMsgToCustomerAdmin(String corpId, String msgContent, String dataId) {
        String msgRecipientList = dingtalkMsgRecipientService.getMsgRecipientList(corpId, null);
        sendOneMsg(corpId, msgRecipientList, msgContent, dataId);
    }
}
