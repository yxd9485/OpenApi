package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.AltmanRefundOrderEvent;
import com.fenbeitong.openapi.plugin.func.altman.service.IFuncAltmanOrderService;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundDetailDTO;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 万能订单逆向单事件处理
 *
 * @author lizhen
 * @date 2020/8/1
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class AltmanRefundOrderEventHandler extends EventHandler<AltmanRefundOrderEvent> {

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IFuncAltmanOrderService funcAltmanOrderService;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 911, version = "2.0", orderId = "fbOrderId", status = "bizOrderStatus", statusList = {92}, logicKeys = {"fbOrderId", "bizOrderStatus"})
    @Override
    public boolean process(AltmanRefundOrderEvent event, Object... args) {
        String orderId = event.getFbOrderId();
        Map<String, Object> orderDetail = null;
        try {
            orderDetail = (Map) funcAltmanOrderService.refundDetail(AltmanOrderRefundDetailDTO.builder().refundOrderId(orderId).companyId(event.getCompanyId()).build(), "v_1.0");
            log.info("获取到的万能订单详情:{}", JsonUtils.toJson(orderDetail));
            if (!ObjectUtils.isEmpty(orderDetail)) {
                Map<String, Object> orderInfo = (Map) orderDetail.get("order_info");
                orderInfo.put("order_category_type", 911);
                orderInfo.put("order_category_name", "万能订单");
                orderInfo.put("order_op_type", 20);
                orderInfo.put("order_op_type_name", "逆向单");
                String userName = (String) orderInfo.get("user_name");
                String companyName = (String) orderInfo.get("company_name");
                ThirdCallbackRecord record = new ThirdCallbackRecord();
                record.setType(911);
                record.setTypeName("万能订单");
                record.setOrderId(orderId);
                record.setOrderStatus(event.getBizOrderStatus());
                record.setCompanyId(event.getCompanyId());
                record.setCompanyName(companyName);
                record.setContactName(userName);
                record.setUserName(userName);
                record.setCallbackType(CallbackType.ORDER.getType());
                record.setCallbackData(JsonUtils.toJson(orderDetail));
                callbackRecordDao.saveSelective(record);
                businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
            }
        } catch (Exception e) {
        }
        return true;
    }

}
