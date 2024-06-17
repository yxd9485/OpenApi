//package com.fenbeitong.openapi.plugin.func.callback;
//
//import com.fenbeitong.openapi.plugin.event.core.EventHandler;
//import com.fenbeitong.openapi.plugin.event.order.dto.BankOrderEvent;
//import com.fenbeitong.openapi.plugin.event.order.dto.HotelOrderEvent;
//import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
//import com.fenbeitong.openapi.plugin.func.order.dto.BankOrderListReqDTO;
//import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
//import com.fenbeitong.openapi.plugin.func.order.service.FuncBankOrderServiceImpl;
//import com.fenbeitong.openapi.plugin.func.order.service.FuncHotelOrderServiceImpl;
//import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
//import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
//import com.fenbeitong.openapi.plugin.util.JsonUtils;
//import com.fenbeitong.openapi.plugin.util.MapUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 虚拟卡信息消息事件
// */
//@ServiceAspect
//@Component
//@Slf4j
//@SuppressWarnings("all")
//public class BankOrderEventHandler extends EventHandler<BankOrderEvent> {
//    @Autowired
//    private FuncBankOrderServiceImpl funcBankOrderService;
//
//    @Autowired
//    private ThirdCallbackRecordDao callbackRecordDao;
//
//    @FuncOrderCallBack(companyId = "companyId", type = 126, status = "status", statusList = {80})
//    @Override
//    public boolean process(BankOrderEvent event) {
//        BankOrderListReqDTO orderDetailReqDTO = new BankOrderListReqDTO();
//        orderDetailReqDTO.setOrderId(event.getOrderId());
//        Map<String, Object> list = (Map<String, Object>) funcBankOrderService.publicPayListOrder(orderDetailReqDTO);
//        Map detail = null;
//        if (!ObjectUtils.isEmpty(list)) {
//            List<Map> results = (List<Map>) list.get("results");
//            detail = results.get(0);
//            if (!ObjectUtils.isEmpty(detail)) {
//                Map orderInfo = (Map) MapUtils.getValueByExpress(detail, "order_info");
//                orderInfo.put("order_category_type", 126);
//                orderInfo.put("order_category_name", "虚拟卡");
//                orderInfo.put("order_op_type", orderInfo.get("bank_trans_type"));
//                orderInfo.put("order_op_type_name", (Integer) orderInfo.get("bank_trans_type") == 2 ? "逆向单" : "正向单");
//            }
//        }
//        ThirdCallbackRecord record = new ThirdCallbackRecord();
//        record.setType(126);
//        record.setTypeName("虚拟卡");
//        record.setOrderId(event.getOrderId());
//        record.setCompanyId(event.getCompanyId());
//        record.setCompanyName((String)detail.get("company_name"));
//        record.setUserName((String)detail.get("user_name"));
//        record.setCallbackType(1);
//        record.setCallbackData(JsonUtils.toJson(detail));
//        callbackRecordDao.saveSelective(record);
//        return true;
//    }
//}
