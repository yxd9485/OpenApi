package com.fenbeitong.openapi.plugin.definition.service;/**
 * <p>Title: OrderCheckService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/26 10:47 下午
 */

import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillDataListDTO;
import com.fenbeitong.openapi.plugin.support.bill.dto.QueryOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.bill.service.StereoBillService;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2021/8/26
 */
@Component
public class OrderCheckService {


    @Autowired
    private StereoBillService stereoBillService;

    @Autowired
    private ThirdCallbackRecordDao thirdCallbackRecordDao;

    @Autowired
    private ThirdCallbackConfDao thirdCallbackConfDao;


//    public void get() {
//        List<ThirdCallbackConf> thirdCallbackConf = thirdCallbackConfDao.queryByCallBackType(CallbackType.ORDER.getType());
//        if (!ObjectUtils.isEmpty(thirdCallbackConf)) {
//            for ()
//        }
//    }

    public Map<String, Object> checkOrder(String companyId, String billNo) {
        //获取全量企业ID
        //获取账单
        List<BillDataListDTO> bill = getBillDetail(companyId, billNo, 1);
        //对比
        Map<String, Object> result = compareOrderWithBill(companyId, bill);
        return result;
    }

    public List<BillDataListDTO> getBillDetail(String companyId, String billNo, int pageIndex) {
        QueryOrderDetailReqDTO queryOrderDetailReq = new QueryOrderDetailReqDTO();
        queryOrderDetailReq.setPageSize(500);
        queryOrderDetailReq.setCompanyId(companyId);
        queryOrderDetailReq.setBillNo(billNo);
        queryOrderDetailReq.setPageIndex(pageIndex);
        BasePageDTO<BillDataListDTO> queryOrderDetailRes = stereoBillService.queryBillDataListDetail(queryOrderDetailReq);
        List<BillDataListDTO> billList = queryOrderDetailRes.getDtoList();
        Integer count = queryOrderDetailRes.getCount();
        int totalPages = count / 500 + 1;
        if (pageIndex < totalPages) {
            billList.addAll(getBillDetail(companyId, billNo, pageIndex + 1));
        }
        return billList;
    }

//    public void queryBillNo(String companyId) {
//        Calendar calendar = Calendar.getInstance();
//        String startMonth = DateUtils.toStr(calendar.getTime(), "yyyyMM");
//        calendar.add(Calendar.MONTH, 1);
//        String endMonth = DateUtils.toStr(calendar.getTime(), "yyyyMM");
//
//        QueryBillNoListReqDTO queryBillNoListReq = new QueryBillNoListReqDTO();
//        queryBillNoListReq.setAppId(companyId);
//        queryBillNoListReq.setStartMonth();
//        queryBillNoListReq.setEndMonth();
//        queryBillNoListReq.setPageIndex(1);
//        queryBillNoListReq.setPageSize(10);
//        stereoBillService.queryBillNo(queryBillNoListReq)
//    }

    public Map<String, Object> compareOrderWithBill(String companyId, List<BillDataListDTO> bill) {
        Map<String, Object> result = new HashMap<>();
        List<BillDataListDTO> lostOrderList = Lists.newArrayList();
        List<Map<String, Object>> errorOrderList = Lists.newArrayList();
        result.put("lost", lostOrderList);
        result.put("error", errorOrderList);
        for (BillDataListDTO billData : bill) {
            String orderId = billData.getOrderId();
            Integer orderCategory = billData.getOrderCategory();
            String customerName = billData.getCustomerName();
            Map<String, Object> condition = new HashMap<>();
            condition.put("companyId", companyId);
            condition.put("orderId", orderId);
            if (orderCategory.equals(7) || orderCategory.equals(15)) {
                condition.put("contactName", customerName);
            }
            ThirdCallbackRecord callBackRecord = thirdCallbackRecordDao.getOrderByCondition(condition);
            if (callBackRecord == null) {
                lostOrderList.add(billData);
            } else {
                BigDecimal companyPayPriceBill = billData.getCompanyPayPrice();
                String callbackData = callBackRecord.getCallbackData();
                Map order = JsonUtils.toObj(callbackData, Map.class);
                Map<String, Object> priceInfo = (Map) order.get("price_info");
                BigDecimal companyPayPriceOrder = BigDecimal.ZERO;
                if (priceInfo != null) {
                    companyPayPriceOrder = BigDecimal.valueOf(NumericUtils.obj2double(priceInfo.get("total_price")));
                }
                if (companyPayPriceBill.compareTo(companyPayPriceOrder) != 0) {
                    Map<String, Object> errorOrder = new HashMap<>();
                    errorOrder.put("bill", billData);
                    errorOrder.put("order", order);
                    errorOrderList.add(errorOrder);
                }
            }
        }
        return result;
    }


}
