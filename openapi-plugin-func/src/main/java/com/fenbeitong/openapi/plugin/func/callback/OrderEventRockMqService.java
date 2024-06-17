package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.core.config.RocketMQConfig;
import com.fenbeitong.openapi.plugin.core.constant.RocketMQConstant;
import com.fenbeitong.openapi.plugin.core.util.RocketMQMessageSendUtil;
import com.fenbeitong.openapi.plugin.func.callback.dto.RocketMQCheckOrderEventMessage;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>Title: OrderEventRockMqService</p>
 * <p>Description: 订单事件消息处理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/12/10 6:08 PM
 */
@Service
@Slf4j
public class OrderEventRockMqService {

    @Autowired
    private RocketMQMessageSendUtil mqMessageSendUtil;

    @Autowired(required = false)
    private RocketMQConfig rocketMQConfig;

    public void sendCheckMsg(String collectionName, String id, int times, Integer status) {
        if (Objects.isNull(rocketMQConfig)) {
            return;
        }
        RocketMQCheckOrderEventMessage message = new RocketMQCheckOrderEventMessage();
        message.setId(id);
        message.setStatus(status);
        message.setCollectionName(collectionName);
        message.setTimes(times);
        log.info("{}-[{}]发送失败重试消息:{}", collectionName, id, JsonUtils.toJson(message));
        //5分钟后检查是否存盘成功
        boolean retryMessageSendSuccess = mqMessageSendUtil.sendNormalDelayMessage(rocketMQConfig.getTopicCheckOrderEvent(), RocketMQConstant.RocketMQTagConstant.TAG_CHECK_ORDER_EVENT, message, 9);
        log.info("{}-[{}]发送失败重试消息:{}，结果:{}", collectionName, id, JsonUtils.toJson(message), retryMessageSendSuccess);
    }
}
