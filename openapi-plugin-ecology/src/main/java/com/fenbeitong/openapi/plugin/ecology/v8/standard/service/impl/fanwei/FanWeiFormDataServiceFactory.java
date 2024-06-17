package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 泛微表单构造器工厂
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@ServiceAspect
@Service
public class FanWeiFormDataServiceFactory {

    private static final Map<String, FanWeiFormDataBuildService> serviceMap = new HashMap<>();

    public FanWeiFormDataServiceFactory(List<FanWeiFormDataBuildService> formDataBuildServices){
        for (FanWeiFormDataBuildService formDataBuildService: formDataBuildServices) {
            serviceMap.put(formDataBuildService.getType(),formDataBuildService);
        }
    }

    public FanWeiFormDataBuildService getServiceByType(String type){
        return serviceMap.get(type);
    }
}
