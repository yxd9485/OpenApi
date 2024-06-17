package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.core.trace.TraceHelper;
import com.fenbeitong.openapi.plugin.event.order.dto.MallOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.service.FuncMallOrderServiceImpl;
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
import java.util.Objects;

/**
 * <p>Title: MallOrderEventHandler</p>
 * <p>Description: 采购订单事件</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/4 4:19 PM
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class MallOrderEventHandler extends EventHandler<MallOrderEvent> {

    @Autowired
    private FuncMallOrderServiceImpl funcMallOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 20, status = "status", statusList = {4202}, logicKeys = {"orderId", "status"})
    @Override
    public boolean process(MallOrderEvent event, Object... args) {
        log.info("[{}][1.0]采购正向单处理状态[4202]", TraceHelper.getTraceId());
        Map<String, Object> orderDetail = (Map<String, Object>) funcMallOrderService.detailOrder(event.getOrderId(),event.getCompanyId(), order -> {
            if (Objects.nonNull(order)) {
                event.setCompanyName(Objects.nonNull(order.getCompany()) ? order.getCompany().getName() : null);
                event.setUserName(Objects.nonNull(order.getBookingPerson()) ? order.getBookingPerson().getId() : null);
                event.setConsigneeName(Objects.nonNull(order.getConsignee()) ? order.getConsignee().getName() : null);
            }
        });
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            orderInfo.put("order_category_type", 20);
            orderInfo.put("order_category_name", "采购");
            orderInfo.put("order_op_type", 10);
            orderInfo.put("order_op_type_name", "正向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(20);
            record.setTypeName("采购");
            record.setOrderId(event.getOrderId());
            record.setOrderStatus(event.getStatus());
            record.setCompanyId(event.getCompanyId());
            record.setCompanyName(event.getCompanyName());
            record.setContactName(event.getConsigneeName());
            record.setUserName(event.getUserName());
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(orderDetail));
            callbackRecordDao.saveSelective(record);
            log.info("[{}][1.0]采购正向单推送数据:{}", TraceHelper.getTraceId(), record.getCallbackData());
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }
}
