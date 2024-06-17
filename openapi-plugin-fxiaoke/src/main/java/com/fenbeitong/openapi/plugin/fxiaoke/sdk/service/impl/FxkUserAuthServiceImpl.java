package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseCode;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception.OpenApiFxkException;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeAuthRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeGetUserInfoByCodeReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeGetUserInfoByCodeRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeGetUserInfoRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkSyncService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkUserAuthService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.po.company.CompanyEmployee;
import com.fenbeitong.usercenter.api.service.employee.IREmployeeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/12/23
 */
@ServiceAspect
@Service
public class FxkUserAuthServiceImpl implements IFxkUserAuthService {


    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Autowired
    private FxiaokeCorpAppDao fxiaokeCorpAppDao;

    @Autowired
    private RestHttpUtils httpUtils;

    @Autowired
    private IFxkSyncService fxkSyncService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @DubboReference(check = false)
    private IREmployeeService employeeService;

    @Override
    public FxiaokeAuthRespDTO auth(String code, String appId, String state) {
        // 获取配置信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("appId", appId);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getFxiaokeCorpApp(map);
        if (fxiaokeCorpApp == null) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_CORP_UN_REGIST);
        }
        String appSecret = fxiaokeCorpApp.getAppSecret();
        String thirdEmployeeId = getUserInfoByCode(code, appId, appSecret);
        PluginCorpDefinition corpByThirdCorpId = pluginCorpDefinitionDao.getCorpByThirdCorpId(fxiaokeCorpApp.getCorpId());
        String companyId = corpByThirdCorpId.getAppId();
        if (useNickNameToEmployeeNumber(companyId)) {
            thirdEmployeeId = transNickName(thirdEmployeeId, fxiaokeCorpApp.getCorpId(), companyId);
        }
        return FxiaokeAuthRespDTO.builder().companyId(companyId).thirdEmployeeId(thirdEmployeeId).build();
    }

    /**
     * 通过code获取用户信息
     *
     * @param code
     * @param appId
     */
    public String getUserInfoByCode(String code, String appId, String appSecret) {
        FxiaokeGetUserInfoByCodeReqDTO fxiaokeGetUserInfoByCodeReqDTO = new FxiaokeGetUserInfoByCodeReqDTO();
        fxiaokeGetUserInfoByCodeReqDTO.setCode(code);
        fxiaokeGetUserInfoByCodeReqDTO.setCorpAccessToken(appSecret);
        fxiaokeGetUserInfoByCodeReqDTO.setCorpId(appId);
        String res = RestHttpUtils.postJson(fxiaokeHost.concat("/oauth2.0/getUserInfoByCode"), JsonUtils.toJson(fxiaokeGetUserInfoByCodeReqDTO));
        FxiaokeGetUserInfoByCodeRespDTO fxiaokeGetUserInfoByCodeRespDTO = JsonUtils.toObj(res, FxiaokeGetUserInfoByCodeRespDTO.class);
        if (fxiaokeGetUserInfoByCodeRespDTO == null ||  !Integer.valueOf(0).equals(fxiaokeGetUserInfoByCodeRespDTO.getErrorCode())) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_EMPLOYEE_NOT_EXISTS);
        }
        return fxiaokeGetUserInfoByCodeRespDTO.getData();
    }

    /**
     * 使用纷享销客openUserId转出nickName, 再用此字段当做工号，从uc转出三方ID
     * @param openUserId
     * @param corpId
     * @return
     */
    public String transNickName(String openUserId, String corpId, String companyId) {
        FxiaokeGetUserInfoRespDTO userByOpenUserId = fxkSyncService.getUserByOpenUserId(openUserId, corpId);
        if (userByOpenUserId == null || userByOpenUserId.getErrorCode() != 0) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_EMPLOYEE_NOT_EXISTS);
        }
        String nickName = userByOpenUserId.getNickName();
        if (StringUtils.isBlank(nickName)) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_EMPLOYEE_NOT_EXISTS, "别名为空");
        }

        List<CompanyEmployee> companyEmployees = employeeService.queryCompanyEmployeeListByEmployeeNum(companyId, Lists.newArrayList(nickName));
        if(ObjectUtils.isEmpty(companyEmployees)) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_EMPLOYEE_NOT_EXISTS, "人员未加入分贝通组织机构");
        }
        return companyEmployees.get(0).getThird_employee_id();
    }

    @Override
    public boolean useNickNameToEmployeeNumber(String companyId) {
        String openSysConfigByTypeCode = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigType.FXIAOKE_FREE_ACCESS_USE_NICK_NAME_TO_EMPLOYEE_NUMBER.getType(), companyId);
        if (openSysConfigByTypeCode != null) {
            return true;
        }
        return false;
    }
}
