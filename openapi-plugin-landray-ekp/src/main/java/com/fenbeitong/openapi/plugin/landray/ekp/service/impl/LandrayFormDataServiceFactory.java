package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fenbeitong.openapi.plugin.landray.ekp.service.LandrayFormDataBuildService;
import com.fenbeitong.openapi.plugin.landray.ekp.service.form.LandrayCarFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.landray.ekp.service.form.LandrayDinnerFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.landray.ekp.service.form.LandrayMallFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.landray.ekp.service.form.LandrayOrderFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.ServiceTypeConstant;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.common.AbstractRevertServiceFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 蓝凌表单构造器工厂
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@Component
public class LandrayFormDataServiceFactory extends AbstractRevertServiceFactory {

    private static final Map<String, LandrayFormDataBuildService> serviceMap = new HashMap<>();

    public LandrayFormDataServiceFactory(){
        serviceMap.put(ServiceTypeConstant.MALL,new LandrayMallFormDataServiceImpl());
        serviceMap.put(ServiceTypeConstant.DINNER,new LandrayDinnerFormDataServiceImpl());
        serviceMap.put(ServiceTypeConstant.CAR,new LandrayCarFormDataServiceImpl());
        serviceMap.put(ServiceTypeConstant.ORDER,new LandrayOrderFormDataServiceImpl());
    }

    public LandrayFormDataBuildService getServiceByType(String type){
        return serviceMap.get(type);
    }

}
