package com.fenbeitong.openapi.plugin.welink.isv.listener;

import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvBaseRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * app_event_push
 * Created by lizhen on 2020/4/2.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvAppPushEventsListener {

    @Autowired
    private WeLinkIsvMessageService weLinkIsvMessageService;

    //@KafkaListener(group = "${group.id:default}", topics = {"app_push_events"})
    public void pushApplyListener(ConsumerRecord<?, ?> record) throws Exception {
        log.info("【push信息】接收到app_push_events消息：{},[消息 来自kafkatopic:{},分区:{}, offset:{}, key:{}]消息内容如下:{}",
                Thread.currentThread().getName(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value());
        Map<String, Object> map = JsonUtils.toObj((String) record.value(), Map.class);
        Map data = (Map) map.get("data");
        String title = StringUtils.obj2str(data.get("title"));
        String content = StringUtils.obj2str(data.get("content"));
        String msg = StringUtils.obj2str(data.get("msg"));
        String userId = StringUtils.obj2str(data.get("userId"));
        String companyId = StringUtils.obj2str(data.get("companyId"));
        KafkaPushMsg kafkaPushMsg = new KafkaPushMsg();
        kafkaPushMsg.setCompanyId(companyId);
        kafkaPushMsg.setUserId(userId);
        kafkaPushMsg.setMsg(msg);
        kafkaPushMsg.setTitle(title);
        kafkaPushMsg.setContent(content);
        kafkaPushMsg.setMsgType(WeLinkIsvConstant.MSG_TYPE_ORDER);
        WeLinkIsvBaseRespDTO weLinkIsvBaseRespDTO = weLinkIsvMessageService.pushMessage(kafkaPushMsg);
        log.info("【push信息】welink isv消息推送处理结束,返回{}", JsonUtils.toJson(weLinkIsvBaseRespDTO));
    }

}
