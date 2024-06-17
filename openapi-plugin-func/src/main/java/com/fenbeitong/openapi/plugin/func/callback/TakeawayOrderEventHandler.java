package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.TakeawayOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncTakeawayOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.bill.constants.OrderCategory;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: TakeawayOrderEventHandler</p>
 * <p>Description: 外卖订单事件</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/4 4:19 PM
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class TakeawayOrderEventHandler extends EventHandler<TakeawayOrderEvent> {

    @Autowired
    private FuncTakeawayOrderServiceImpl takeawayOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @FuncOrderCallBack(companyId = "companyId", type = 50, status = "status", statusList = {80}, logicKeys = {"orderId", "status"})
    @Override
    public boolean process(TakeawayOrderEvent event, Object... args) {
        String orderId = event.getOrderId();
        String lockKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(RedisKeyConstant.ORDER_CALLBACK_LOCK_KEY, OrderCategory.TAKEOUT, 10, orderId));
        Long lockTime = RedisDistributionLock.lock(lockKey, redisTemplate);
        if (lockTime > 0) {
            try {
                String orderKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(RedisKeyConstant.ORDER_CALLBACK_KEY, OrderCategory.TAKEOUT, 10, orderId));
                String orderRedis = StringUtils.obj2str(redisTemplate.opsForValue().get(orderKey));
                if (!StringUtils.isBlank(orderRedis)) {
                    log.info("订单已处理，不再推送，orderId:", orderId);
                    return true;
                }
                OrderDetailReqDTO req = new OrderDetailReqDTO();
                req.setOrderId(event.getOrderId());
                req.setCompanyId(event.getCompanyId());
                Map<String, Object> orderDetail = (Map<String, Object>) takeawayOrderService.detail(req);
                if (!ObjectUtils.isEmpty(orderDetail)) {
                    Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
                    orderInfo.put("order_category_type", 50);
                    orderInfo.put("order_category_name", "外卖");
                    orderInfo.put("order_op_type", 10);
                    orderInfo.put("order_op_type_name", "正向单");
                    ThirdCallbackRecord record = new ThirdCallbackRecord();
                    record.setType(50);
                    record.setTypeName("外卖");
                    record.setOrderId(event.getOrderId());
                    record.setOrderStatus(event.getStatus());
                    record.setCompanyId(event.getCompanyId());
                    record.setCompanyName(event.getCompanyName());
                    record.setContactName(event.getConsigneeName());
                    record.setUserName(event.getUserName());
                    record.setCallbackType(CallbackType.ORDER.getType());
                    record.setCallbackData(JsonUtils.toJson(orderDetail));
                    callbackRecordDao.saveSelective(record);
                    businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
                    redisTemplate.opsForValue().set(orderKey, "1");
                    redisTemplate.expire(orderKey, 1, TimeUnit.DAYS);
                }
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【外卖订单推送】未获取到锁，orderId={}", orderId);
        }
        return true;
    }

    public static void main(String[] args) {
        String orderKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(RedisKeyConstant.ORDER_CALLBACK_KEY, OrderCategory.TAKEOUT, 10, "orderId"));

        System.out.println(orderKey);
    }
}
