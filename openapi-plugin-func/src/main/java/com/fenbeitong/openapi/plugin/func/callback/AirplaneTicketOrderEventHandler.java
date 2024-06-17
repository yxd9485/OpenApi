package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.AirplaneTicketOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncAirOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.CallbackThirdSupportService;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 机票消息事件
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class AirplaneTicketOrderEventHandler extends EventHandler<AirplaneTicketOrderEvent> {

    @Autowired
    private FuncAirOrderServiceImpl funcAirOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 7, status = "status", statusList = {1800, 1811, 1821, 1823}, logicKeys = {"orderId", "ticketId", "status"})
    @Override
    public boolean process(AirplaneTicketOrderEvent event, Object... args) {
        AirOrderDetailReqDTO req = new AirOrderDetailReqDTO();
        req.setOrderId(event.getOrderId());
        req.setCompanyId(event.getCompanyId());
        req.setTicketId(event.getTicketId());
        req.setIsIntl(false);
        //saas延迟，等3秒
        ThreadUtils.sleep(3, TimeUnit.SECONDS);
        Map<String, Object> orderDetail = (Map<String, Object>) funcAirOrderService.detail(req);
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            orderInfo.put("order_category_type", 7);
            orderInfo.put("order_category_name", "国内机票");
            orderInfo.put("order_op_type", event.getStatus() == 1811 ? 20 : 10);
            orderInfo.put("order_op_type_name", event.getStatus() == 1811 ? "逆向单" : "正向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(7);
            record.setTypeName("国内机票");
            record.setOrderId(event.getOrderId());
            record.setTicketId(event.getTicketId());
            record.setOrderStatus(event.getStatus());
            record.setCompanyId(event.getCompanyId());
            record.setCompanyName(event.getCompanyName());
            record.setContactName(event.getPassengerName());
            record.setUserName(event.getUserName());
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(orderDetail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }
}
