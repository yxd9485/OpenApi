package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: CommonServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-01-05 16:42
 */

@ServiceAspect
@Service
public class CommonServiceImpl {

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    /**
     * 获取token
     */
    String getToken(String companyId) {

        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.GET_CUSTOMIZE_COMPANY_TOKEN.getType());
        configMap.put("state", 1);
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        if (ObjectUtils.isEmpty(openSysConfig)) {
            throw new FinhubException(1, "宁波伟立组织架构获取token配置失败");
        }
        String data = RestHttpUtils.get(openSysConfig.getValue(), new HttpHeaders(), null);
        Map dataMap = JsonUtils.toObj(data, Map.class);
        String token = dataMap.get("access_token").toString();
        return token;
    }
}
