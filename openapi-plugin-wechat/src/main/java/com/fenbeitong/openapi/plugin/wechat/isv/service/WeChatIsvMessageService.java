package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvSendMessageRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvSendMessageResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;
import java.util.Optional;

/**
 * Created by lizhen on 2020/3/28.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvMessageService {

    private static final String SEND_MESSAGE_URL = "/cgi-bin/message/send?access_token=";

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Value("${host.webappwechat}")
    private String webappwechatHost;

    @Autowired
    private WeChatIsvHttpUtils weChatIsvHttpUtils;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;


    /**
     * 向企业微信推送消息
     *
     * @return
     */
    public WeChatIsvSendMessageResponse pushMessage(WebAppPushEvents kafkaPushMsg) {
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(kafkaPushMsg.getMsgType())) {
            String companyId = kafkaPushMsg.getReceiveCompanyId();
            String userId = kafkaPushMsg.getUserId();
            String title = kafkaPushMsg.getTitle();
            String content = kafkaPushMsg.getContent();
            if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
                log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
                return null;
            }
            //查询企业授权信息
            WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
            if (weChatIsvCompany == null) {
                log.info("【push信息】非企业微信isv企业,companyId:{}", companyId);
                return null;
            }
            Integer agentId = weChatIsvCompany.getAgentid();
            String corpId = weChatIsvCompany.getCorpId();
            WeChatIsvSendMessageRequest weChatIsvSendMessageRequest = new WeChatIsvSendMessageRequest();
            weChatIsvSendMessageRequest.setTouser(kafkaPushMsg.getThirdEmployeeId());
            weChatIsvSendMessageRequest.setAgentId(agentId);
            weChatIsvSendMessageRequest.setMsgType("textcard");
            initTextCardMsg(weChatIsvSendMessageRequest, kafkaPushMsg);
            if (StringUtils.isBlank(weChatIsvSendMessageRequest.getTextCard().getUrl())) {
                weChatIsvSendMessageRequest.setTextCard(null);
                weChatIsvSendMessageRequest.setMsgType("text");
                WeChatIsvSendMessageRequest.Text text = new WeChatIsvSendMessageRequest.Text();
                text.setContent(kafkaPushMsg.getContent());
                weChatIsvSendMessageRequest.setText(text);
            }
            return sendMessage(weChatIsvSendMessageRequest, corpId);
        }
        return null;
    }

    /**
     * 初始化textcard消息
     *
     * @param weChatIsvSendMessageRequest
     * @param kafkaPushMsg
     */
    private void initTextCardMsg(WeChatIsvSendMessageRequest weChatIsvSendMessageRequest, KafkaPushMsg kafkaPushMsg) {
        log.info("isv接收到消息内部消息的消息体为：{}", kafkaPushMsg);
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        content = "<div class=\"gray\">" + DateUtils.toSimpleStr(DateUtils.now()) + "</div> <div class=\"normal\">" + content + "</div>";
        String btntxt = "查看详情";

        WeChatIsvSendMessageRequest.Textcard textcard = new WeChatIsvSendMessageRequest.Textcard();
        textcard.setTitle(title);
        textcard.setDescription(content);
        textcard.setBtntxt(btntxt);
        weChatIsvSendMessageRequest.setTextCard(textcard);
//        String msgType = kafkaPushMsg.getMsgType();//2:消费通知 4:审批通知 8:订单通知 32:系统通知
//        if (WeChatIsvConstant.WECHAT_ISV_MSG_TYPE_ORDER.equals(msgType)) {
//            initOrderUrl(textcard, kafkaPushMsg);
//        } else if (WeChatIsvConstant.WECHAT_ISV_MSG_TYPE_APPLY.equals(msgType)) {
//            initApplicationUrl(textcard, kafkaPushMsg);
//        }
        String uri = webappwechatHost + WeChatIsvConstant.WECHAT_ISV_APP_HOME;
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        textcard.setUrl(messageUrl);
    }


    /**
     * 初始化订单跳转的url
     *
     * @param textcard
     * @param kafkaPushMsg
     */
    private void initOrderUrl(WeChatIsvSendMessageRequest.Textcard textcard, KafkaPushMsg kafkaPushMsg) {
        String msg = kafkaPushMsg.getMsg();
        Map map = JsonUtils.toObj(msg, Map.class);
        if (map != null) {
            String orderType = StringUtils.obj2str(map.get("order_type"));
            String orderId = StringUtils.obj2str(map.get("order_id"));
            if (!StringUtils.isBlank(orderType) && !StringUtils.isBlank(orderId)) {
                int orderTypeInt = Integer.valueOf(orderType);
                OrderType ot = OrderType.getEnum(orderTypeInt);
                switch (ot) {
                    case Air:
                        textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_ORDER_AIR + orderId);
                        break;
                    case Hotel:
                        textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_ORDER_HOTEL + orderId);
                        break;
                    case Taxi:
                        textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_ORDER_TAXI + orderId);
                        break;
                    case Train:
                        textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_ORDER_TRAIN + orderId);
                        break;
                }
            }
        }
    }

    /**
     * 初始化审批跳转的url
     *
     * @param textcard
     * @param kafkaPushMsg
     */
    private void initApplicationUrl(WeChatIsvSendMessageRequest.Textcard textcard, KafkaPushMsg kafkaPushMsg) {
        String msg = kafkaPushMsg.getMsg();
        Map map = JsonUtils.toObj(msg, Map.class);
        if (map != null) {
            Integer applyType = (Integer) map.get("apply_type");
            String id = StringUtils.obj2str(map.get("id"));
            String settingType = StringUtils.obj2str(map.get("setting_type"));
            String viewType = StringUtils.obj2str(map.get("view_type"));
            //saas_push的view_type，1申请人，2审批人，3抄送人。 给前端跳转的type，1.审批人，2申请人，3抄送人
            if ("1".equals(viewType)) {
                viewType = "2";
            } else if ("2".equals(viewType)) {
                viewType = "1";
            }
            if ("1".equals(settingType)) {
                if (applyType != null && !StringUtils.isBlank(id)) {
                    if (applyType == 1) {
                        textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_APPLICATION_TRIP + id + "&type=" + viewType);
                    } else if (applyType == 12) {
                        textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_APPLICATION_TAXI + id + "&type=" + viewType);
                    }
                }
            } else if ("2".equals(settingType)) {//订单审批
                String orderType = StringUtils.obj2str(map.get("order_type"));
                if (orderType != null && !StringUtils.isBlank(id)) {
                    textcard.setUrl(webappwechatHost + WeChatIsvConstant.WECHAT_ISV_MESSAGE_URL_APPLICATION_DETAIL + id + "&type=" + viewType);
                }
            }
        }
    }


    /**
     * 向微信推消息
     *
     * @param weChatIsvSendMessageRequest
     * @param corpId
     * @return
     */
    public WeChatIsvSendMessageResponse sendMessage(WeChatIsvSendMessageRequest weChatIsvSendMessageRequest, String corpId) {
        return sendMessage(JsonUtils.toJson(weChatIsvSendMessageRequest), corpId);
    }

    public WeChatIsvSendMessageResponse sendMessage(String message, String corpId) {
        String res = weChatIsvHttpUtils.postJsonWithAccessToken(wechatHost + SEND_MESSAGE_URL, message, corpId);
        WeChatIsvSendMessageResponse weChatIsvSendMessageResponse = (WeChatIsvSendMessageResponse) JsonUtils.toObj(res, WeChatIsvSendMessageResponse.class);
        if (weChatIsvSendMessageResponse == null || Optional.ofNullable(weChatIsvSendMessageResponse.getErrcode()).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_SEND_MESSAGE_FAILED));
        }
        return weChatIsvSendMessageResponse;
    }

    public void sendInstallSuccessMessage(String thirdEmployeeId, Integer agentId, String corpId) {
        try {
            String message = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WECHAT_ISV_MESSAGE_INSTALL_SUCCESS.getCode());
            message = String.format(message, thirdEmployeeId, agentId);
            sendMessage(message, corpId);
        } catch (Exception e) {
            log.error("微信消息推送失败：", e);
        }

    }

}
