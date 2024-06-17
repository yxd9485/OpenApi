package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkFreeLoginDto;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiUserService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.auth.constant.FreeLoginConstant;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaokechun
 * @date 2018/11/20 19:42
 */
@Slf4j
@RestController
@RequestMapping("/dingtalk/auth")
public class DingtalkAuthController {

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private IApiUserService apiUserService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;


    /**
     * 钉钉用户免登入口
     * 返回钉钉用户ID
     *
     * @param corpId
     * @param authCode
     */
    @RequestMapping("/getLoginUser")
    public Object getLoginUser(@RequestParam("corpId") String corpId, @RequestParam("authCode") String authCode) {
        log.info("钉钉用户登录请求：corpId: {}, authCode: {}", corpId, authCode);
        PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCorpId(corpId);
        // 1.检查企业状态,只有已完成数据初始化的企业才可以登录
        if (dingtalkCorp == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.CORP_INALID);
        }
        if (dingtalkCorp.getState() == 0) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.CORP_UNINITIALIZED);
        }
        // 其他异常状态
        if (dingtalkCorp.getState() != 1) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.CORP_INALID);
        }
        Map<String, String> loginMap = new HashMap<>(2);
        loginMap.put("companyId", dingtalkCorp.getAppId());
        String companyId = dingtalkCorp.getAppId();
        // 免登字段
        OpenThirdScriptConfig freeAccountConfig = openThirdScriptConfigDao.getCommonScriptConfig(dingtalkCorp.getAppId(), EtlScriptType.USER_FREE_LOGIN);
        boolean hasScriptConfig = null != freeAccountConfig;
        DingtalkFreeLoginDto dingtalkFreeLoginDto = DingtalkFreeLoginDto.builder()
                .companyId(companyId)
                .build();
        LoginResVO loginResVo = new LoginResVO();
        if ( !hasScriptConfig ){
            // 2.根据corpId及授权码获取登录用户ID
            String userId = apiUserService.getAuthUserId(corpId, authCode);
            loginResVo = openEmployeeService.loginAuthInitWithChannelInfo(companyId, userId, "1", CompanyLoginChannelEnum.DINGTALK_H5);
        } else {
            // 免登字段从脚本取
            JSONObject configJson = JSONObject.parseObject(freeAccountConfig.getParamJson());
            String freeLoginLabelValue = apiUserService.getAuthFreeLoginLabel(corpId, authCode, companyId, FreeLoginConstant.FREE_LOGIN , freeAccountConfig );
            String configKey = "";
            if ( null != configJson && null != configJson.get(FreeLoginConstant.FREE_LOGIN)){
                configKey = (String) configJson.get(FreeLoginConstant.FREE_LOGIN);
                loginMap.put(configKey, freeLoginLabelValue);
            }
            dingtalkFreeLoginDto.setHasScriptConfig(hasScriptConfig);
            dingtalkFreeLoginDto.setConfigKey(configKey);
            dingtalkFreeLoginDto.setFreeLoginLabelValue(freeLoginLabelValue);
            loginResVo = apiUserService.getLoginInfo(dingtalkFreeLoginDto);
        }
        return DingtalkResponseUtils.success(loginResVo);
    }

}
