package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.core.config.RocketMQConfig;
import com.fenbeitong.openapi.plugin.core.constant.RocketMQConstant;
import com.fenbeitong.openapi.plugin.event.core.EventBusCenter;
import com.fenbeitong.openapi.plugin.func.callback.dto.RocketMQCheckOrderEventMessage;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * <p>Title: RocketMQOrderEventConsumer</p>
 * <p>Description: 订单事件消费客户端</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/12/10 1:59 PM
 */
@Slf4j
@Component
public class RocketMQOrderEventConsumer {

    @Autowired(required = false)
    RocketMQConfig rocketMQConfig;

    @Autowired
    private EventBusCenter eventBusCenter;

    @PostConstruct
    public void consumeSettlementTopic() throws Exception {
        if (Objects.isNull(rocketMQConfig)) {
            return;
        }
        if (!rocketMQConfig.getConsumerEnable()) {
            return;
        }
        log.info("rocketmq消费者订单事件启动");
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(rocketMQConfig.getTopicCheckOrderEventConsumerGroup());
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConfig.getNameServerAddress());
        // * 代表不过滤
        defaultMQPushConsumer.subscribe(rocketMQConfig.getTopicCheckOrderEvent(), "*");
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    byte[] body = msg.getBody();
                    String tags = msg.getTags();
                    // 根据标签tag来决定什么操作
                    executeByTags(tags, body);
                } catch (Exception e) {
                    log.info("订单事件消息消费失败,异常原因", e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        log.info(String.format("消费者{%s}启动了", rocketMQConfig.getTopicCheckOrderEvent()));
    }

    private void executeByTags(String tags, byte[] body) {
        String message = new String(body);
        log.info("订单事件-tags={},message={}", tags, message);
        if (RocketMQConstant.RocketMQTagConstant.TAG_CHECK_ORDER_EVENT.equals(tags)) {
            RocketMQCheckOrderEventMessage event = JsonUtils.toObj(message, RocketMQCheckOrderEventMessage.class);
            eventBusCenter.postSync(event);
        }
    }

}
