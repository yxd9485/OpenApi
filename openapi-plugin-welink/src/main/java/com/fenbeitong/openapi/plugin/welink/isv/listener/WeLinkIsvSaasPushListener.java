package com.fenbeitong.openapi.plugin.welink.isv.listener;

import com.fenbeitong.finhub.kafka.consumer.KafkaConsumerUtils;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * Created by lizhen on 2020/4/3.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvSaasPushListener {
    @Autowired
    private WeLinkIsvMessageService weLinkIsvMessageService;
    //@KafkaListener(group = "${group.id:default}", topics = {"saas_push"})
    public void pushApplyListener(ConsumerRecord<?, ?> record) throws Exception {
        log.info("【push信息】消费者线程:{},[消息 来自kafkatopic:{},分区:{}, offset:{}, key:{}]消息内容如下:{}",
                Thread.currentThread().getName(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value());
        KafkaPushMsg kafkaPushMsg = KafkaConsumerUtils.invokeIMessage(record.value().toString(), KafkaPushMsg.class);
        if (kafkaPushMsg != null) {
            kafkaPushMsg.setMsgType(WeLinkIsvConstant.MSG_TYPE_APPLY);
            weLinkIsvMessageService.pushMessage(kafkaPushMsg);
        }
    }
}
