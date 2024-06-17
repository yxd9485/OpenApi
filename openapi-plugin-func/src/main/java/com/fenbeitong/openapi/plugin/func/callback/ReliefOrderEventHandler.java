package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.ReliefOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncReliefOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@SuppressWarnings("all")
public class ReliefOrderEventHandler extends EventHandler<ReliefOrderEvent> {

    @Autowired
    private FuncReliefOrderServiceImpl funcReliefOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 912, orderId = "fbOrderId", status = "bizOrderStatus", statusList = {80, -256}, logicKeys = {"fbOrderId", "bizOrderStatus"})
    @Override
    public boolean process(ReliefOrderEvent event, Object... args) {
        OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
        orderDetailReqDTO.setCompanyId(event.getCompanyId());
        orderDetailReqDTO.setOrderId(event.getFbOrderId());
        Map<String, Object> detail = (Map<String, Object>) funcReliefOrderService.detail(orderDetailReqDTO);
        if (!ObjectUtils.isEmpty(detail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(detail, "order_info");
            Map userInfo = (Map) MapUtils.getValueByExpress(detail, "user_info");
            Object consumerInfo = MapUtils.getValueByExpress(detail, "consumer_info");
            String userName = StringUtils.obj2str(userInfo.get("name"));
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            if (consumerInfo instanceof List) {
                List<Map> consumerList = (List) consumerInfo;
                String consumerName = ObjectUtils.isEmpty(consumerList) ? null : StringUtils.obj2str(consumerList.get(0).get("name"));
                record.setContactName(consumerName);
            }
            orderInfo.put("order_category_type", 912);
            orderInfo.put("order_category_name", "减免订单");
            orderInfo.put("order_op_type", event.getBizOrderStatus() == 80 ? 10 : 20);
            orderInfo.put("order_op_type_name", event.getBizOrderStatus() == 80 ? "正向单" : "逆向单");
            record.setType(912);
            record.setTypeName("减免订单");
            record.setOrderId(event.getFbOrderId());
            record.setOrderStatus(event.getBizOrderStatus());
            record.setCompanyId(event.getCompanyId());
            record.setUserName(userName);
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(detail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }
}
