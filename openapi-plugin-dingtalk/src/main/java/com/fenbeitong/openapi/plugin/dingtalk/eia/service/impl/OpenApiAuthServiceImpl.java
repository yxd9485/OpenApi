package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.OpenApiResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luastar.swift.base.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaokechun
 * @date 2018/11/20 19:47
 */
@Slf4j
@ServiceAspect
@Service
public class OpenApiAuthServiceImpl extends AbstractEmployeeService {

    @Value("${host.openapi}")
    private String hostOpenapi;

    @Autowired
    private RestHttpUtils httpUtils;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

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
     * @param companyId 企业ID
     * @param data      要发送的数据
     * @return Map<String, String> 鉴权参数
     */
    public Map<String, String> genApiAuthParams(String companyId, String data) {
        PluginCorpDefinition company = dingtalkCorpService.getByCompanyId(companyId);
        return genApiAuthParamsWithEmployee(companyId, data, company.getAdminId(), true);
    }

    /**
     * 生成open api 鉴权参数
     *
     * @param companyId   企业ID
     * @param data        要请求的数据
     * @param employeeId  员工ID
     * @param fbtEmployee 是否为分贝通员工ID
     * @return
     */
    public MultiValueMap genApiAuthParamsWithEmployee(String companyId, String data, String employeeId, boolean fbtEmployee) {
        log.info("获取是否为分贝用户原始类型: {}", fbtEmployee);
        log.info("获取是否为分贝用户请求数据: {}", data);
        log.info("获取是否为分贝用户Id: {}", employeeId);
        MultiValueMap params = new LinkedMultiValueMap();
        PluginCorpDefinition company = dingtalkCorpService.getByCompanyId(companyId);
        long timestamp = System.currentTimeMillis();
        String sign = SignTool.genSign(timestamp, data, company.getSignKey());
        String accessToken = this.getAccessToken(company.getAppId(), company.getAppKey());
        String employeeType = fbtEmployee ? "0" : "1";
        log.info("获取是否为分贝用户类型 {}", employeeType);
        params.add("timestamp", String.valueOf(timestamp));
        params.add("access_token", accessToken);
        params.add("sign", sign);
        params.add("employee_id", employeeId);
        params.add("employee_type", employeeType);
        return params;
    }


}
