package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.core.enums.YesOrNo;
import com.fenbeitong.openapi.plugin.definition.dto.company.auth.AuthRegisterReqDTO;
import com.fenbeitong.openapi.plugin.definition.dto.company.auth.AuthDefinitionInfoDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PhoneValidateDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PhoneValidate;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

import static com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode.APP_ID_UNAUTH;
import static com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode.AUTH_INFO_ALREADY_EXIST;
import static com.fenbeitong.openapi.plugin.support.company.enums.AuthStatus.DISABLE;
import static com.fenbeitong.openapi.plugin.support.company.enums.AuthStatus.ENABLE;

/**
 * 企业授权配置服务
 * Created by log.chang on 2019/12/13.
 */
@ServiceAspect
@Service
public class AuthDefinitionService {

    @Value("${host.app.web.url}")
    private String webHost;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;
    @Autowired
    private PhoneValidateDao phoneValidateDao;

    /**
     * 授权
     */
    public synchronized AuthDefinitionInfoDTO register(AuthRegisterReqDTO req) {
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(req.getAppId());
        if (authDefinition != null)
            throw new OpenApiDefinitionException(AUTH_INFO_ALREADY_EXIST, req.getAppId() + ":" + req.getAppName());

        // 公司授权信息
        String appKey = RandomUtils.bsonId();
        //生成公司signKey,（根据最新修改后的去掉了randomLong()方法，换用其他的方法进行调用，传入参数可以指定位数）
        String md5Hex = RandomUtils.randomNum(10) + req.getAppId();
        String signKey = DigestUtils.md5Hex(md5Hex);
        authDefinition = AuthDefinition.builder()
                .appId(req.getAppId())
                .appName(req.getAppName())
                .appKey(appKey)
                .signKey(signKey)
                .appUrl(getStringUrl())
                .appStatus(ENABLE.getState())
                .appRemark(req.getAppRemark())
                .build();
        authDefinitionDao.saveSelective(authDefinition);

        // 是否虚拟号码
        Date now = DateUtils.now();
        PhoneValidate phoneValidate = PhoneValidate.builder()
                .appId(req.getAppId())
                .phoneValidate(YesOrNo.yesOrNo(req.getVirtualNumber()) ? 1 : 0)
                .operatorId("")
                .operationTime(now).build();
        phoneValidateDao.saveSelective(phoneValidate);

        return AuthDefinitionInfoDTO.builder()
                .appId(authDefinition.getAppId())
                .appName(authDefinition.getAppName())
                .appKey(appKey)
                .signKey(signKey)
                .status(authDefinition.getAppStatus()).build();
    }

    /**
     * 生成URL（未知逻辑及作用，暂时保留）
     *
     * @return OpenAuthInfo
     */
    private String getStringUrl() {
        return webHost + "?token=服务器颁发Token";
    }

    /**
     * 禁用
     */
    public synchronized AuthDefinitionInfoDTO disable(String appId) {
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(appId);
        if (authDefinition == null)
            throw new OpenApiDefinitionException(APP_ID_UNAUTH, appId);
        if (DISABLE.getState() != NumericUtils.obj2int(authDefinition.getAppStatus())) {
            authDefinition.setAppStatus(DISABLE.getState());
            authDefinitionDao.updateById(authDefinition);
        }
        return AuthDefinitionInfoDTO.builder()
                .appId(authDefinition.getAppId())
                .appName(authDefinition.getAppName())
                .appKey(authDefinition.getAppKey())
                .signKey(authDefinition.getSignKey())
                .status(authDefinition.getAppStatus()).build();
    }

    /**
     * 启用
     */
    public synchronized AuthDefinitionInfoDTO enable(String appId) {
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(appId);
        if (authDefinition == null)
            throw new OpenApiDefinitionException(APP_ID_UNAUTH, appId);
        if (ENABLE.getState() != NumericUtils.obj2int(authDefinition.getAppStatus(), -1)) {
            authDefinition.setAppStatus(ENABLE.getState());
            authDefinitionDao.updateById(authDefinition);
        }
        return AuthDefinitionInfoDTO.builder()
                .appId(authDefinition.getAppId())
                .appName(authDefinition.getAppName())
                .appKey(authDefinition.getAppKey())
                .signKey(authDefinition.getSignKey())
                .status(authDefinition.getAppStatus()).build();
    }

    /**
     * 企业授权详情
     */
    public AuthDefinitionInfoDTO getAuthInfo(String appId) {
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(appId);
        if (authDefinition == null)
            throw new OpenApiDefinitionException(APP_ID_UNAUTH, appId);
        return AuthDefinitionInfoDTO.builder()
                .appId(authDefinition.getAppId())
                .appName(authDefinition.getAppName())
                .appKey(authDefinition.getAppKey())
                .signKey(authDefinition.getSignKey())
                .status(authDefinition.getAppStatus()).build();
    }

}
