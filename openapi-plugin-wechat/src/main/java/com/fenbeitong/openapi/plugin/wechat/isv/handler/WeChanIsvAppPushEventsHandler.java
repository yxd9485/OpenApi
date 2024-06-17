//package com.fenbeitong.openapi.plugin.wechat.isv.handler;
//
//import com.fenbeitong.eventbus.event.common.AppPushEvent;
//import com.fenbeitong.eventbus.util.IEventHandler;
//import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
//import com.finhub.framework.core.SpringUtils;
//import com.fenbeitong.openapi.plugin.util.JsonUtils;
//import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
//import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvSendMessageResponse;
//import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvMessageService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
//
///**
// * app_event_push
// * Created by lizhen on 2020/4/2.
// */
//@ServiceAspect
//@Service
//@Slf4j
//public class WeChanIsvAppPushEventsHandler implements IEventHandler<AppPushEvent> {
//
//    private WeChatIsvMessageService weChatIsvMessageService = SpringUtils.getBean(WeChatIsvMessageService.class);
//
//    @Override
//    public void handle(AppPushEvent appPushEvent) {
//        log.info("【push信息】,接收到app_push_events消息,companyId={},userId={},msg={},title={},content={},msgType={}",
//                appPushEvent.companyId(), appPushEvent.userId(), appPushEvent.msg(), appPushEvent.title(), appPushEvent.content(), appPushEvent.msgType());
//        if (appPushEvent != null) {
//            KafkaPushMsg kafkaPushMsg = new KafkaPushMsg();
//            if(appPushEvent.companyId().isDefined()) {
//                kafkaPushMsg.setCompanyId(appPushEvent.companyId().get());
//            }
//            kafkaPushMsg.setUserId(appPushEvent.userId());
//            if (appPushEvent.msg().isDefined()) {
//                kafkaPushMsg.setMsg(appPushEvent.msg().get());
//            }
//            kafkaPushMsg.setTitle(appPushEvent.title());
//            kafkaPushMsg.setContent(appPushEvent.content());
//            if(appPushEvent.msgType().isDefined()) {
//                kafkaPushMsg.setMsgType(appPushEvent.msgType().get());
//            }
//            kafkaPushMsg.setMsgType(WeChatIsvConstant.WECHAT_ISV_MSG_TYPE_APPLY);
//            WeChatIsvSendMessageResponse weChatIsvSendMessageResponse = weChatIsvMessageService.pushMessage(kafkaPushMsg);
//            log.info("【push信息】企业微信isv消息推送处理结束,返回{}", JsonUtils.toJson(weChatIsvSendMessageResponse));
//        }
//    }
//
//}
