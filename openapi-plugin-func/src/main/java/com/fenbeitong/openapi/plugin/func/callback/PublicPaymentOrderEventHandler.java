package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.PublicPaymentOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.PublicPaymentOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncPublicPaymentOrderServiceImpl;
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

import java.util.List;
import java.util.Map;

/**
 * 虚拟卡信息消息事件
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class PublicPaymentOrderEventHandler extends EventHandler<PublicPaymentOrderEvent> {

    @Autowired
    private FuncPublicPaymentOrderServiceImpl funcPublicPaymentOrderService;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @FuncOrderCallBack(companyId = "companyId", type = 128, status = "orderStatus", statusList = {80}, logicKeys = {"orderId", "orderStatus"})
    @Override
    public boolean process(PublicPaymentOrderEvent event, Object... args) {
        PublicPaymentOrderListReqDTO orderDetailReqDTO = new PublicPaymentOrderListReqDTO();
        orderDetailReqDTO.setOrderId(event.getOrderId());
        Map<String, Object> list = (Map<String, Object>) funcPublicPaymentOrderService.list(orderDetailReqDTO);
        Map detail = null;
        if (!ObjectUtils.isEmpty(list)) {
            List<Map> results = (List<Map>) list.get("results");
            detail = results.get(0);
            if (!ObjectUtils.isEmpty(detail)) {
                Map orderInfo = (Map) MapUtils.getValueByExpress(detail, "order_info");
                orderInfo.put("order_category_type", 128);
                orderInfo.put("order_category_name", "对公支付");
                orderInfo.put("order_op_type", orderInfo.get("bank_trans_type"));
                orderInfo.put("order_op_type_name", (Integer) orderInfo.get("bank_trans_type") == 2 ? "逆向单" : "正向单");
            }
        }
        ThirdCallbackRecord record = new ThirdCallbackRecord();
        record.setType(128);
        record.setTypeName("对公支付");
        record.setOrderId(event.getOrderId());
        record.setOrderStatus(event.getOrderStatus());
        record.setCompanyId(event.getCompanyId());
        record.setCompanyName((String) detail.get("company_name"));
        record.setUserName((String) detail.get("user_name"));
        record.setCallbackType(CallbackType.ORDER.getType());
        record.setCallbackData(JsonUtils.toJson(detail));
        callbackRecordDao.saveSelective(record);
        businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        return true;
    }
}
