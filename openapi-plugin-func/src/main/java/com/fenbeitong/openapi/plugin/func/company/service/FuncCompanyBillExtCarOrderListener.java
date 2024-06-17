package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.etl.service.impl.DefaultEtlListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: FuncCompanyBillExtCarOrderListener</p>
 * <p>Description: 用车订单扩展字段监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/8 5:00 PM
 */
@ServiceAspect
@Service
public class FuncCompanyBillExtCarOrderListener extends DefaultEtlListener {

    @Autowired
    private BillExtPassengerServiceImpl passengerService;

    @SuppressWarnings("unchecked")
    @Override
    public void afterTransform(Map<String, Object> srcMap, Map transformMap) {
        if (ObjectUtils.isEmpty(transformMap)) {
            return;
        }
//        Map carInfo = (Map) transformMap.get("car");
//        String passengerId = (String) carInfo.get("passengerId");
//        if (ObjectUtils.isEmpty(passengerId)) {
//            carInfo.put("userId", carInfo.get("employeeId"));
//            carInfo.put("userDeptId", carInfo.get("deptId"));
//        } else {
//            String userId = (String) carInfo.get("userId");
//            if (ObjectUtils.isEmpty(userId)) {
//                passengerService.setPassengerInfo(carInfo);
//            }
//        }
    }
}
