package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.*;
import com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3.FeiShuSingleUserDetailRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.IBuildLoginInfoService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2021/1/22.
 */
@Slf4j
@ServiceAspect
@Service
public class FeiShuEiaUserAuthService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private IBuildLoginInfoService buildLoginInfoService;

    @Autowired
    private FeiShuEiaCompanyAuthService feiShuEiaCompanyAuthService;

    /**
     * 应用免登
     *
     * @param code
     * @return
     */
    public LoginResVO auth(String code, String corpId) {
        FeiShuAuthenRespDTO.FeiShuAuthenData feiShuAuthenData = webLoginValidate(code, corpId);
        String userId = feiShuAuthenData.getUserId();
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String companyId = pluginCorpDefinition.getAppId();
        FeiShuUserInfoDTO userInfoDTO = getUserInfo(FeiShuConstant.ID_TYPE_OPEN_ID,feiShuAuthenData.getOpenId(),pluginCorpDefinition.getThirdCorpId());
        return buildLoginInfoService.buildLoginInfoWithConfiguration(userInfoDTO,userId,companyId,CompanyLoginChannelEnum.LARK_H5);
    }

    private FeiShuAuthenRespDTO.FeiShuAuthenData webLoginValidate(String code, String corpId) {
        String url = feishuHost + FeiShuConstant.LOGIN_VALIDATE_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("grant_type", "authorization_code");
        param.put("code", code);
        String res = feiShuEiaHttpUtils.postJsonWithAppAccessToken(url, param, corpId);
        FeiShuAuthenRespDTO feiShuAuthenRespDTO = JsonUtils.toObj(res, FeiShuAuthenRespDTO.class);
        if (feiShuAuthenRespDTO == null || 0 != feiShuAuthenRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.WEB_LOGIN_VALIDATE_FAILED);
        }
        return feiShuAuthenRespDTO.getData();
    }

    public FeiShuUserInfoDTO getUserInfo(String idType, String id, String corpId) {
        FeiShuUserBatchGetRespDTO feiShuUserBatchGetRespDTO = userBatchGet(idType, Lists.newArrayList(id), corpId);
        List<FeiShuUserInfoDTO> userInfos = feiShuUserBatchGetRespDTO.getData().getUserInfos();
        if (!ObjectUtils.isEmpty(userInfos)) {
            return userInfos.get(0);
        }
        return null;
    }

    public FeiShuUserBatchGetRespDTO userBatchGet(String idType, List<String> openIdList, String corpId) {
        String url = feishuHost + FeiShuConstant.USER_BATCH_GET;
        StringBuffer idsSb = new StringBuffer();
        for (String id : openIdList) {
            if (idsSb.length() != 0) {
                idsSb.append("&");
            }
            idsSb.append(idType + "=").append(id);
        }
        url = url + idsSb.toString();
        String res = feiShuEiaHttpUtils.getWithTenantAccessToken(url, null, corpId);
        FeiShuUserBatchGetRespDTO feiShuUserBatchGetRespDTO = JsonUtils.toObj(res, FeiShuUserBatchGetRespDTO.class);
        if (feiShuUserBatchGetRespDTO == null || 0 != feiShuUserBatchGetRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_USER_BATCH_GET_FAILED);
        }
        return feiShuUserBatchGetRespDTO;
    }

    public FeiShuUserDetailRespDTO userBatchGetDetailByPhoneOrEmail( String phone, String email, String corpId) {
        String url = feishuHost + FeiShuConstant.GET_USER_INFO_DETAIL;
        StringBuffer idsSb = new StringBuffer();
        if (!StringUtils.isBlank(phone)){
            idsSb.append("mobiles=").append(phone);
        }
        if (!StringUtils.isBlank(email)){
            idsSb.append("emails=").append(email);
        }
        url = url + idsSb.toString();
        String res = feiShuEiaHttpUtils.getWithTenantAccessToken(url, null, corpId);
        FeiShuUserDetailRespDTO feiShuUserDetailRespDTO = JsonUtils.toObj(res, FeiShuUserDetailRespDTO.class);
        if (feiShuUserDetailRespDTO == null || 0 != feiShuUserDetailRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_USER_INFO_DETAIL_FAILED);
        }
        return feiShuUserDetailRespDTO;
    }

    /**
     * 通过手机号或者邮箱获取用户id
     * @param appId
     * @param appSecret
     * @param phones 员工手机号集合
     * @return 员工详情DTO
     */
    public FeiShuUserContactRespDTO userSingleDetailGet(String appId ,  String appSecret , List<String> phones) {
        String url = feishuHost + FeiShuConstant.BATCH_GET_ID;
        String tenantAccessToken = feiShuEiaCompanyAuthService.getTenantAccessTokenByAppIdAndSecret( appId, appSecret );
        Map<String,Object> map =  new HashMap<>();
        map.put("mobiles" , phones);
        String res = feiShuEiaHttpUtils.postJsonWithToken(url, JsonUtils.toJson(map), tenantAccessToken);
        FeiShuUserContactRespDTO feiShuUserContactRespDTO = JsonUtils.toObj(res, FeiShuUserContactRespDTO.class);
        if (feiShuUserContactRespDTO == null || 0 != feiShuUserContactRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_SINGLE_USER_INFO_FAILED);
        }
        return feiShuUserContactRespDTO;
    }

}
