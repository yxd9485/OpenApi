package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.MeishiOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.MeiShiOrderDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.MeiShiRefundDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.MeiShiRefundDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncMeiShiOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: MeishiOrderEventHandler</p>
 * <p>Description: 美食订单事件处理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/14 12:09 PM
 */
@SuppressWarnings("all")
@Component
public class MeishiOrderEventHandler extends EventHandler<MeishiOrderEvent> {


    @Autowired
    private FuncMeiShiOrderServiceImpl funcMeiShiOrderService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @FuncOrderCallBack(companyId = "companyId", type = 60, status = "status", statusList = {80, 95}, logicKeys = {"orderId", "status"})
    @Override
    public boolean process(MeishiOrderEvent event, Object... args) {
        //正向单
        boolean zxd = event.getStatus() == 80;
        MeiShiOrderDTO meiShiOrderDTO = funcMeiShiOrderService.detailOrder(event.getOrderId());
        Map meishiOrderMap = JsonUtils.toObj(JsonUtils.toJson(meiShiOrderDTO), Map.class);
        Object meishiRefundOrder = funcMeiShiOrderService.detailRefund(MeiShiRefundDetailReqDTO.builder().refundOrderId(event.getOrderId()).apiVersion("v_1.0").build());
        Map meishiRefundMap = JsonUtils.toObj(JsonUtils.toJson((MeiShiRefundDTO) meishiRefundOrder), Map.class);
        Map<String, Object> orderDetail = zxd
            ? meishiOrderMap
            : meishiRefundMap;
        if (!ObjectUtils.isEmpty(orderDetail)) {
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            Map<String, Object> userInfo = (Map) MapUtils.getValueByExpress(orderDetail, "user_info");
            String userName = StringUtils.obj2str(userInfo.get("name"));
            orderInfo.put("order_category_type", 60);
            orderInfo.put("order_category_name", "到店");
            orderInfo.put("order_op_type", zxd ? 10 : 20);
            orderInfo.put("order_op_type_name", zxd ? "正向单" : "逆向单");
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(60);
            record.setTypeName("到店");
            record.setOrderId(event.getOrderId());
            record.setOrderStatus(event.getStatus());
            record.setCompanyId(event.getCompanyId());
            record.setUserName(userName);
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(orderDetail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
        return true;
    }
}
