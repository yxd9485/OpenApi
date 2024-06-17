package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.HotelOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncHotelOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 酒店消息事件
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class HotelOrderEventHandler extends EventHandler<HotelOrderEvent> {
    @Autowired
    private FuncHotelOrderServiceImpl funcHotelOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 11, status = "status", statusList = {2501, 2800}, logicKeys = {"orderId",  "status"})
    @Override
    public boolean process(HotelOrderEvent event, Object... args) {
        OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
        orderDetailReqDTO.setCompanyId(event.getCompanyId());
        orderDetailReqDTO.setOrderId(event.getOrderId());
        Map<String, Object> detail = (Map<String, Object>) funcHotelOrderService.detail(orderDetailReqDTO);
        if (!ObjectUtils.isEmpty(detail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(detail, "order_info");
            orderInfo.put("order_category_type", 11);
            orderInfo.put("order_category_name", "酒店");
            orderInfo.put("order_op_type", event.getStatus() == 2800 ? 20 : 10);
            orderInfo.put("order_op_type_name", event.getStatus() == 2800 ? "逆向单" : "正向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(11);
            record.setTypeName("酒店");
            record.setOrderId(event.getOrderId());
            record.setOrderStatus(event.getStatus());
            record.setCompanyId(event.getCompanyId());
            record.setCompanyName(event.getCompanyName());
            record.setContactName(event.getPassengerName());
            record.setUserName(event.getUserName());
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(detail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }
}
