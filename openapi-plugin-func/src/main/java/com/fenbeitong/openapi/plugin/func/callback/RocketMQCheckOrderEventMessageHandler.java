package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.func.callback.dto.RocketMQCheckOrderEventMessage;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.core.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: RocketMQCheckOrderEventMessageHandler</p>
 * <p>Description: 检查订单事件</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/12/10 7:53 PM
 */
@Component
@Slf4j
public class RocketMQCheckOrderEventMessageHandler extends EventHandler<RocketMQCheckOrderEventMessage> {

    @Autowired
    private OrderEventRockMqService orderEventRockMqService;

    @Autowired
    private ThirdCallbackRecordDao thirdCallbackRecordDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Override
    public boolean process(RocketMQCheckOrderEventMessage message, Object... args) {
        String collectionName = message.getCollectionName();
        Map eventMap = mongoTemplate.findById(message.getId(), Map.class, collectionName);
        Map data = (Map) eventMap.get("data");
        String companyId = (String) data.get("companyId");
        String orderId = (String) data.get("orderId");
        String ticketId = (String) data.get("ticketId");
        Integer status = message.getStatus() == null ? 0 : message.getStatus();
        Example example = new Example(ThirdCallbackRecord.class);
        example.createCriteria().andEqualTo("companyId", companyId).andEqualTo("orderId", orderId);
        List<ThirdCallbackRecord> thirdCallbackRecords = thirdCallbackRecordDao.listByExample(example);
        boolean success = false;
        if (!ObjectUtils.isEmpty(thirdCallbackRecords)) {
            if (ObjectUtils.isEmpty(ticketId)) {
                success = thirdCallbackRecords.stream().anyMatch(r -> status.equals(r.getOrderStatus()));
            } else {
                success = thirdCallbackRecords.stream().anyMatch(r -> status.equals(r.getOrderStatus()) && ticketId.equals(r.getTicketId()));
            }
        }
        if (!success) {
            Integer times = message.getTimes();
            if (times > 4) {
                String msg = collectionName + "存盘失败:companyId=" + companyId + ";orderId=" + orderId;
                if (!ObjectUtils.isEmpty(ticketId)) {
                    msg += ",ticketId=" + ticketId;
                }
                exceptionRemind.remindDingTalk(msg);
            } else {
                handEvent(eventMap, data);
                sendCheckMsg(eventMap, message.getId(), collectionName, times, status);
            }
        }
        return true;
    }

    private void sendCheckMsg(Map eventMap, String id, String collectionName, Integer times, Integer status) {
        if (ObjectUtils.isEmpty(eventMap)) {
            return;
        }
        orderEventRockMqService.sendCheckMsg(collectionName, id, times + 1, status);
    }

    private void handEvent(Map eventMap, Map data) {
        if (ObjectUtils.isEmpty(eventMap)) {
            return;
        }
        String eventClassName = (String) eventMap.get("class");
        String processClassName = (String) eventMap.get("processClass");
        String processMethodName = (String) eventMap.get("processMethod");
        try {
            Class<?> eventClass = Class.forName(eventClassName);
            Class<?> processClass = Class.forName(processClassName);
            Object processBean = SpringUtils.getBean(processClass);
            Method processMethod = processClass.getMethod(processMethodName, eventClass, Object[].class);
            processMethod.invoke(processBean, JsonUtils.toObj(JsonUtils.toJson(data), eventClass), new Object[]{true});
        } catch (Exception e) {
            log.warn("订单事件存盘重试失败", e);
        }

    }
}
