package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.IntlAirTicketOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncAirOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: IntlAirTicketOrderEventHandler</p>
 * <p>Description: 国际机票订单事件</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/1 4:19 PM
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class IntlAirTicketOrderEventHandler extends EventHandler<IntlAirTicketOrderEvent> {

    @Autowired
    private FuncAirOrderServiceImpl funcAirOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 40, status = "status", statusList = {1800, 1811, 1821}, logicKeys = {"orderId", "ticketId", "status"})
    @Override
    public boolean process(IntlAirTicketOrderEvent event, Object... args) {
        log.info("开始处理,事件内容:{}",event);
        AirOrderDetailReqDTO req = new AirOrderDetailReqDTO();
        req.setCompanyId(event.getCompanyId());
        req.setOrderId(event.getOrderId());
        req.setTicketId(event.getTicketId());
        req.setIsIntl(true);
        ThreadUtils.sleep(2, TimeUnit.SECONDS);
        Map<String, Object> orderDetail = (Map<String, Object>) funcAirOrderService.detail(req);
        log.info("查询到的国际机票详情orderDetail:{}",orderDetail);
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            orderInfo.put("order_category_type", 40);
            orderInfo.put("order_category_name", "国际机票");
            orderInfo.put("order_op_type", event.getStatus() == 1811 ? 20 : 10);
            orderInfo.put("order_op_type_name", event.getStatus() == 1811 ? "逆向单" : "正向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(40);
            record.setTypeName("国际机票");
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
