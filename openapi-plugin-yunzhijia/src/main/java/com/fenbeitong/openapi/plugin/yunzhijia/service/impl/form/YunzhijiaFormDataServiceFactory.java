package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.form;

import com.fenbeitong.openapi.plugin.support.revert.apply.constant.ServiceTypeConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormDataBuilderService;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
@ServiceAspect
@Service
public class YunzhijiaFormDataServiceFactory {

    private static final Map<String, IYunzhijiaFormDataBuilderService> serviceMap = new HashMap<>();

    public YunzhijiaFormDataServiceFactory(){
        serviceMap.put(ServiceTypeConstant.DINNER,new YunzhijiaDinnerFormBuilderServiceImpl());
        serviceMap.put(ServiceTypeConstant.MALL,new YunzhijiaMallFormBuilderServiceImpl());
        serviceMap.put(ServiceTypeConstant.TAKE_OUT,new YunzhijiaTakeOutFormBuilderServiceImpl());
    }

    public IYunzhijiaFormDataBuilderService getServiceByType(String type){
        return serviceMap.get(type);
    }
}
