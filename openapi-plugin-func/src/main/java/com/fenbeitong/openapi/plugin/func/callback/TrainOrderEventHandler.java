package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.TrainOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.TrainOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncTrainOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 火车票事件处理
 *
 * @author lizhen
 * @date 2020/8/1
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class TrainOrderEventHandler extends EventHandler<TrainOrderEvent> {

    @Autowired
    private FuncTrainOrderServiceImpl trainOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @FuncOrderCallBack(companyId = "companyId", type = 15, version = "2.0", status = "status", statusList = {3202, 3703, 3801}, logicKeys = {"orderId", "ticketId", "status"})
    @Override
    public boolean process(TrainOrderEvent event, Object... args) {
        String orderId = event.getOrderId();
        String ticketId = event.getTicketId();
        String companyId = event.getCompanyId();
        TrainOrderDetailReqDTO trainOrderDetailReqDTO = new TrainOrderDetailReqDTO();
        trainOrderDetailReqDTO.setCompanyId(companyId);
        trainOrderDetailReqDTO.setTicketId(ticketId);
        trainOrderDetailReqDTO.setOrderId(orderId);
        Map<String, Object> orderDetail = null;
        //重试3次
        int sleepTime = 1;
        for (int i = 0; i < 4; i++) {
            sleepTime = sleepTime * 3;
            ThreadUtils.sleep(sleepTime, TimeUnit.SECONDS);
            try {
                orderDetail = (Map) trainOrderService.detail(trainOrderDetailReqDTO, true);
            } catch (Exception e) {
                log.info("获取订单详情异常：", e);
                String msg = String.format("火车订单回传异常\n原因：未获取到订单详情\n公司id【%s】\n订单id【%d】", companyId, orderId);
                exceptionRemind.remindDingTalk(msg);
                continue;
            }
            //未获取取数据重试
            if (ObjectUtils.isEmpty(orderDetail)) {
                continue;
            }
            //状态不一致重试
            int status = NumericUtils.obj2int(MapUtils.getValueByExpress(orderDetail, "order_info:status"));
            if (event.getStatus().intValue() != status) {
                String msg = String.format("火车订单回传异常\n原因：订单状态异常\n公司id【%s】\n订单id【%d】\n订单状态【%d】", companyId, orderId, status);
                exceptionRemind.remindDingTalk(msg);
                continue;
            }
            break;
        }
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            orderInfo.put("order_category_type", 15);
            orderInfo.put("order_category_name", "火车");
            orderInfo.put("order_op_type", event.getStatus() == 3801 ? 20 : 10);
            orderInfo.put("order_op_type_name", event.getStatus() == 3801 ? "逆向单" : "正向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(15);
            record.setTypeName("火车票");
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

    public static void main(String[] args) {
        ThreadUtils.sleep(0, TimeUnit.SECONDS);
        System.out.println(0);
        ThreadUtils.sleep(1, TimeUnit.SECONDS);
        System.out.println(1);
    }

}
