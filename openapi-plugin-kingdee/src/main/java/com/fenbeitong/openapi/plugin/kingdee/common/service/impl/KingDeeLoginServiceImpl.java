package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeLoginService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: LoginServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/22 11:36 上午
 */
@ServiceAspect
@Service
@Slf4j
public class KingDeeLoginServiceImpl implements KingDeeLoginService {

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    KingdeeService kingdeeService;

    @Autowired
    OpenKingdeeUrlConfigDao openKingdeeUrlConfigDao;
    @Autowired
    private KingdeeConfig kingdeeConfig;

    @Override
    public String gettoken(OpenKingdeeUrlConfig openKingdeeUrlConfig, String baseUrl) {

        if (ObjectUtils.isEmpty(openKingdeeUrlConfig)) {
            throw new FinhubException(500, "获取 openKingdeeUrlConfig 配置异常");
        }
        log.info("companyId:{},openKingdeeUrlConfig:{}", openKingdeeUrlConfig.getCompanyId(), JsonUtils.toJson(openKingdeeUrlConfig));
        KingDeeK3CloudConfigDTO.Login loginDTO = new KingDeeK3CloudConfigDTO.Login();
        BeanUtils.copyProperties(openKingdeeUrlConfig, loginDTO);
        loginDTO.setUsername(openKingdeeUrlConfig.getUserName());
        Map map = JsonUtils.toObj(JsonUtils.toJson(loginDTO), Map.class);
        MultiValueMap multiValueMap = new LinkedMultiValueMap<>();
        map.forEach((k, v) -> {
            multiValueMap.add(k, v);
        });
        ResultVo login = kingdeeService.login(openKingdeeUrlConfig.getUrl() + baseUrl, multiValueMap);
        if (!ObjectUtils.isEmpty(login) && !ObjectUtils.isEmpty(login.getData())) {
            Map<String, Object> map2 = (Map<String, Object>) login.getData();
            String cookie = map2.get("cookie").toString();
            return cookie;
        }

        return null;
    }


    @Override
    public String gettoken(String url, MultiValueMap multiValueMap) {

        log.info("url:{},multiValueMap:{}", url, JsonUtils.toJson(multiValueMap));
        ResultVo login = kingdeeService.login(url, multiValueMap);
        if (!ObjectUtils.isEmpty(login) && !ObjectUtils.isEmpty(login.getData())) {
            Map<String, Object> map2 = (Map<String, Object>) login.getData();
            String cookie = map2.get("cookie").toString();
            return cookie;
        }
        return null;
    }
}

