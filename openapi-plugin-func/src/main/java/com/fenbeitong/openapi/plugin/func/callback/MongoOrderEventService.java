package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: MongoOrderEventService</p>
 * <p>Description: 订单消息存盘</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/12/10 10:46 AM
 */
@Service
@Slf4j
public class MongoOrderEventService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String saveEvent(Object event, String collectionName, List<String> logicKeyValues, String processClass, String processMethod, String orderId, Integer status) {
        log.info("mongodb-order-event存盘,{}", event);
        String logicId = String.join(",", logicKeyValues);
        byte[] bytes = logicId.getBytes(StandardCharsets.UTF_8);
        String id = DigestUtils.md5Hex(bytes);
        Map<String, Object> eventObject = Maps.newHashMap();
        eventObject.put("_id", id);
        eventObject.put("status", status);
        eventObject.put("class", event.getClass().getName());
        eventObject.put("processClass", processClass);
        eventObject.put("processMethod", processMethod);
        Map data = JsonUtils.toObj(JsonUtils.toJson(event), Map.class);
        data.put("orderId", orderId);
        eventObject.put("data", data);
        eventObject.put("createTime", System.currentTimeMillis());
        mongoTemplate.insert(eventObject, collectionName);
        log.info("mongodb-order-event存盘,{},id={}", event, id);
        return id;
    }

}
