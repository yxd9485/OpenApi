package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.ExpressOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.ExpressOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncExpressOrderServiceImpl;
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

@Component
@Slf4j
@SuppressWarnings("all")
public class ExpressOrderEventHandler extends EventHandler<ExpressOrderEvent> {

    @Autowired
    private FuncExpressOrderServiceImpl funcExpressOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 131, status = "status", statusList = {80, 6}, logicKeys = {"orderId", "status"})
    @Override
    public boolean process(ExpressOrderEvent event, Object... args) {
        OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
        orderDetailReqDTO.setCompanyId(event.getCompanyId());
        orderDetailReqDTO.setOrderId(event.getOrderId());
        ExpressOrderDetailReqDTO expressOrderDetailReqDTO = new ExpressOrderDetailReqDTO();
        expressOrderDetailReqDTO.setOrderId(event.getOrderId());
        Map<String, Object> detail = (Map<String, Object>) funcExpressOrderService.deliveryDetailOrder(expressOrderDetailReqDTO,event.getCompanyId());
        if (!ObjectUtils.isEmpty(detail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(detail, "order_info");
            orderInfo.put("order_category_type", 131);
            orderInfo.put("order_category_name", "快递");
            orderInfo.put("order_op_type", event.getStatus() == 80 ? 10 : 20);
            orderInfo.put("order_op_type_name", event.getStatus() == 80 ? "正向单" : "逆向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(131);
            record.setTypeName("快递");
            record.setOrderId(event.getOrderId());
            record.setOrderStatus(event.getStatus());
            record.setCompanyId(event.getCompanyId());
            record.setCompanyName(event.getCompanyName());
            record.setUserName(event.getUserName());
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(detail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }
}
