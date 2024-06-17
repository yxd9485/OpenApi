package com.fenbeitong.openapi.plugin.customize.zhiou.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.zhiou.dao.CustomizeBeisenCorpDao;
import com.fenbeitong.openapi.plugin.customize.zhiou.dao.LandrayEkpConfigDao;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyRequestDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.OpenApiResponse;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.CustomizeBeisenCorp;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luastar.swift.base.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ZhiouOpenApiAuthServiceImpl
 * @Description 致欧openapi鉴权及配置读取
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/30
 **/
@Slf4j
@ServiceAspect
@Service
public class ZhiouOpenApiAuthServiceImpl {

    @Value("${host.openapi}")
    private String hostOpenapi;
    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private LandrayEkpConfigDao landrayEkpConfigDao;

    @Autowired
    private CustomizeBeisenCorpDao customizeBeisenCorpDao;

    private static Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public String getAccessToken(String companyId, String companySecret) {
        String tokenKey = StrUtils.formatString("openapi:token:{0}", companyId);
        String token = cache.getIfPresent(tokenKey);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        token = getTokenFromRemote(companyId, companySecret);
        cache.put(tokenKey, token);
        return token;
    }

    private String getTokenFromRemote(String appId, String appSecret) {
        log.info("调用开放平台获取token接口, 参数：appId: {}, appSecret: {}", appId, appSecret);
        MultiValueMap params = new LinkedMultiValueMap();
        params.add("app_id", appId);
        params.add("app_key", appSecret);
        String jsonText = RestHttpUtils.postFormUrlEncode(hostOpenapi + "/open/api/auth/v1/dispense", null, params);
        log.info("调用开放平台获取token接口完成, 返回结果：{}", jsonText);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OpenApiResponse<String> response = gson.fromJson(jsonText, OpenApiResponse.class);
        if (response != null && response.getCode() == 0) {
            Map token = gson.fromJson(response.getData(), Map.class);
            return (String) token.get("access_token");
        }
        throw new FinhubException(response.getCode(), response.getMsg());
    }


    /**
     * 生成open api鉴权参数
     * 使用企业管理员作为调用者进行签名
     *
     * @param applyRequestDTO 申请单参数
     * @return Map<String, String> 鉴权参数
     */
    public MultiValueMap genApiAuthParams(ApplyRequestDTO applyRequestDTO, String companyId) {
        String employeeId = superAdminUtils.superAdmin(companyId);
        Map dataMap = Maps.newHashMap();
        dataMap.put("apply_id",applyRequestDTO.getApplyId());
        dataMap.put("apply_type",applyRequestDTO.getApplyType());
        return genApiAuthParams(companyId, JsonUtils.toJson(dataMap), employeeId,true);
    }

    /**
     * 生成open api 鉴权参数
     *
     * @param companyId   企业ID
     * @param data        要请求的数据
     * @param employeeId  员工ID
     * @param fbtEmployee 是否为分贝通员工ID
     * @return params 鉴权参数
     */
    public MultiValueMap genApiAuthParams(String companyId, String data, String employeeId, boolean fbtEmployee) {
        log.info("请求数据: {}", data);
        MultiValueMap params = new LinkedMultiValueMap();
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        long timestamp = System.currentTimeMillis();
        String sign = SignTool.genSign(timestamp, data, authDefinition.getSignKey());
        String accessToken = this.getAccessToken(authDefinition.getAppId(), authDefinition.getAppKey());
        Integer employeeType = fbtEmployee ? 0 : 1;
        params.add("timestamp", String.valueOf(timestamp));
        params.add("access_token", accessToken);
        params.add("sign", sign);
        params.add("employee_id", employeeId);
        params.add("employee_type", employeeType);
        params.add("data",data);
        return params;
    }

    /**
     * 根据公司id查询蓝凌配置
     * @param companyId 公司id
     * @return landrayEkpConfig 蓝凌配置
     */
    public OpenLandrayEkpConfig getLandrayEkpConfig(String companyId){
        if(StringUtils.isEmpty(companyId)){
            log.info("companyId为空");
            throw new OpenApiCustomizeException(SupportRespCode.TOKEN_INFO_IS_NULL);
        }
        //校验企业是否存在
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        if (ObjectUtils.isEmpty(authDefinition)) {
            log.info("企业信息不存在,companyId:"+companyId);
            throw new OpenApiCustomizeException(SupportRespCode.TOKEN_INFO_IS_NULL);
        }
        //查询北森的企业配置表，获取openid
        OpenLandrayEkpConfig landrayEkpConfig = landrayEkpConfigDao.getByCompanyId(companyId);
        if(ObjectUtils.isEmpty(landrayEkpConfig)){
            throw new OpenApiCustomizeException(SupportRespCode.COMPANY_SETTING_NOT_EXIST);
        }
        return landrayEkpConfig;
    }

    /**
     * 根据公司id查询北森配置
     * @param companyId 公司id
     * @return beisenCorp 北森配置
     */
    public CustomizeBeisenCorp getBeisenCorp(String companyId){
        if(StringUtils.isEmpty(companyId)){
            log.info("companyId为空");
            throw new OpenApiCustomizeException(SupportRespCode.TOKEN_INFO_IS_NULL);
        }
        //校验企业是否存在
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        if (ObjectUtils.isEmpty(authDefinition)) {
            log.info("企业信息不存在,companyId:"+companyId);
            throw new OpenApiCustomizeException(SupportRespCode.TOKEN_INFO_IS_NULL);
        }
        //查询北森的企业配置表，获取openid
        CustomizeBeisenCorp beisenCorp = customizeBeisenCorpDao.getByCompanyId(companyId);
        if(ObjectUtils.isEmpty(beisenCorp)){
            throw new OpenApiCustomizeException(SupportRespCode.COMPANY_SETTING_NOT_EXIST);
        }
        return beisenCorp;
    }

}
