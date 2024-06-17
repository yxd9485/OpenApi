package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.TaxiOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncCarOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用车事件处理
 *
 * @author li zhen
 * @date 2020/8/1
 */
@SuppressWarnings("all")
@Component
public class TaxiOrderEventHandler extends EventHandler<TaxiOrderEvent> {

    @Autowired
    private FuncCarOrderServiceImpl carOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 3, version = "2.0", status = "status", statusList = {700, 610, 611}, logicKeys = {"orderId",  "status"})
    @Override
    public boolean process(TaxiOrderEvent event, Object... args) {
        String orderId = event.getOrderId();
        String companyId = event.getCompanyId();
        OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
        orderDetailReqDTO.setCompanyId(companyId);
        orderDetailReqDTO.setOrderId(orderId);
        //saas延迟，等3秒
        ThreadUtils.sleep(3, TimeUnit.SECONDS);
        Map<String, Object> orderDetail = (Map) carOrderService.detail(orderDetailReqDTO);
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            orderInfo.put("order_category_type", 3);
            orderInfo.put("order_category_name", "用车");
            orderInfo.put("order_op_type", event.getStatus() == 700 ? 10 : 20);
            orderInfo.put("order_op_type_name", event.getStatus() == 700 ? "正向单" : "逆向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(3);
            record.setTypeName("用车");
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
