package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common.MallFanWeiFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@ServiceAspect
@Service
public class FanWeiNoticeFactoryService {

    private static final Map<String, FanWeiFormDataBuildService> serviceMap = new HashMap<>();

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private MallFanWeiFormDataServiceImpl mallFormDataService;

    public FanWeiNoticeFactoryService(){
        serviceMap.put(CommonServiceTypeConstant.MALL,mallFormDataService);
    }

    public Map<String, String> getNoticeResult(String serviceType, Map map){
        if (CommonServiceTypeConstant.MALL.equals(serviceType)){
            return commonApplyService.parseFbtMallApplyNotice(map);
        }

        if (CommonServiceTypeConstant.TRIP.equals(serviceType)){
            return commonApplyService.parseFbtTripApplyNotice(map);
        }

        if (CommonServiceTypeConstant.CAR.equals(serviceType)){
            return commonApplyService.parseFbtTaxiApplyNotice(map);
        }

        if (CommonServiceTypeConstant.DINNER.equals(serviceType)){
            return commonApplyService.parseFbtDinnerApplyNotice(map);
        }

        if (CommonServiceTypeConstant.ORDER.equals(serviceType)){
            return commonApplyService.parseFbtOrderApplyNotice(map);
        }

        if (CommonServiceTypeConstant.REIMBURSE.equals(serviceType)){
            Map<String,String> result = new HashMap<>();
            String companyId =(String) map.get("company_id");
            String applyId =(String) map.get("apply_id");
            result.put("companyId",companyId);
            result.put("applyId",applyId);
            Map employee = (Map) map.get("employee");
            String employeeThirdId =(String) employee.get("employee_third_id");
            result.put("thirdEmployeeId",employeeThirdId);
            return result;
        }
        return null;
    }
}
