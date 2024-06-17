package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.IKingdeeCommonService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenThirdKingdeeConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.callback.constant.ResultEnum;
import com.fenbeitong.openapi.plugin.support.util.KingdeeBaseUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
@ServiceAspect
@Slf4j
public class KingdeeCommonServiceImpl implements IKingdeeCommonService {

    @Autowired
    private KingdeeService kingdeeService;

    @Autowired
    private KingdeeConfig kingdeeConfig;

    @Autowired
    private OpenThirdKingdeeConfigDao openThirdKingdeeConfigDao;
    /**
     * 登陆并获取cookie
     *
     * @param openThirdKingdeeConfig
     * @return
     */
    public String loginAndGetCookie(OpenThirdKingdeeConfig openThirdKingdeeConfig) {
        MultiValueMap loginParam = KingdeeBaseUtils.buildLogin(openThirdKingdeeConfig.getAcctId(), openThirdKingdeeConfig.getUserName(),
            openThirdKingdeeConfig.getPassword(), Long.valueOf(openThirdKingdeeConfig.getLcid()));

        String loginUrl = openThirdKingdeeConfig.getUrl() + kingdeeConfig.getLogin();
        ResultVo login = kingdeeService.login(loginUrl, loginParam);
        if (login.getCode() != ResultEnum.SUCCESS.getCode()) {
            log.warn("【登录金蝶系统失败】：{}", login.getMsg());
            return null;
        }

        // 获取cookie
        Map<String, Object> loginMap = (Map<String, Object>) login.getData();
        return loginMap.get("cookie").toString();
    }


    /**
     * 获取金蝶配置
     *
     * @param companyId
     * @return
     */
    public OpenThirdKingdeeConfig getOpenThirdKingdeeConfig(String companyId) {
        OpenThirdKingdeeConfig openThirdKingdeeConfig = openThirdKingdeeConfigDao.getOpenThirdKingdeeConfig(
            new HashMap<String, Object>() {{
                put("companyId", companyId);
            }});
        if (openThirdKingdeeConfig == null) {
            throw new OpenApiArgumentException("找不到金蝶配置信息");
        }
        return openThirdKingdeeConfig;
    }
}
