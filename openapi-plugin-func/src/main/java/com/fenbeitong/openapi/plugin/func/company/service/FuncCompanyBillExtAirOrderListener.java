package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.etl.service.impl.DefaultEtlListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: FuncCompanyBillExtAirOrderListener</p>
 * <p>Description: 机票订单扩展字段监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/8 5:00 PM
 */
@ServiceAspect
@Service
public class FuncCompanyBillExtAirOrderListener extends DefaultEtlListener {

    @Autowired
    private BillExtPassengerServiceImpl passengerService;

    @SuppressWarnings("unchecked")
    @Override
    public void afterTransform(Map<String, Object> srcMap, Map transformMap) {
//        if (ObjectUtils.isEmpty(transformMap)) {
//            return;
//        }
//        Map airInfo = (Map) transformMap.get("air");
//        passengerService.setPassengerInfo(airInfo);
    }

}
