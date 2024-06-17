package com.fenbeitong.openapi.plugin.landray.ekp.service;

import com.fenbeitong.openapi.plugin.landray.ekp.service.impl.LandrayConfigInfoUtiImpl;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.ConfigInfoUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/8/12
 */
@Component
public class ConfigInfoFactory {

    private static final Map<OpenType,ConfigInfoUtil> serviceMap = new HashMap<>();

    public ConfigInfoFactory() {
        serviceMap.put(OpenType.LANDARY_EKP,new LandrayConfigInfoUtiImpl());
    }

    public String getUrlInfo(OpenType openType){
        if ( null == openType ){
            return null;
        }
        return serviceMap.get(openType).getUrl();
    }

}
