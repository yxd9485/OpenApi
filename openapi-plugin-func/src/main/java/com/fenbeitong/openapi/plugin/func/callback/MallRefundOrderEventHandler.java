package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.MallRefundOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.MallRefundDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncMallOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 采购售后单事件处理
 *
 * @author lizhen
 * @date 2020/8/1
 */
@SuppressWarnings("all")
@Component
public class MallRefundOrderEventHandler extends EventHandler<MallRefundOrderEvent> {

    @Autowired
    private FuncMallOrderServiceImpl funcMallOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 20, version = "2.0", orderId = "fbOrderId", status = "bizOrderStatus", statusList = {50}, logicKeys = {"fbOrderId", "bizOrderStatus"})
    @Override
    public boolean process(MallRefundOrderEvent event, Object... args) {
        String orderId = event.getFbOrderId();
        Map<String, Object> orderDetail = (Map) funcMallOrderService.detailRefund(MallRefundDetailReqDTO.builder().refundOrderId(orderId).apiVersion("v_1.0").build(),event.getCompanyId());
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            orderInfo.put("order_category_type", 20);
            orderInfo.put("order_category_name", "采购");
            orderInfo.put("order_op_type", 20);
            orderInfo.put("order_op_type_name", "逆向单");
            Map<String, Object> userInfo = (Map) orderDetail.get("user_info");
            String userName = (String) userInfo.get("name");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(20);
            record.setTypeName("采购");
            record.setOrderId(orderId);
            record.setOrderStatus(event.getBizOrderStatus());
            record.setCompanyId(event.getCompanyId());
            record.setContactName(userName);
            record.setUserName(userName);
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(orderDetail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }

}
