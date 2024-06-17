package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.FeiShuFormDataBuildService;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.impl.DinnerFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.impl.MallFormDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * 飞书表单构造器工厂
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@ServiceAspect
@Service
public class FeiShuFormDataServiceFactory {

    private static final Map<String, FeiShuFormDataBuildService> serviceMap = new HashMap<>();

    public FeiShuFormDataServiceFactory(){
        serviceMap.put(FeiShuServiceTypeConstant.MALL,new MallFormDataServiceImpl());
        serviceMap.put(FeiShuServiceTypeConstant.DINNER,new DinnerFormDataServiceImpl());
    }

    public FeiShuFormDataBuildService getServiceByType(String type){
        return serviceMap.get(type);
    }
}
