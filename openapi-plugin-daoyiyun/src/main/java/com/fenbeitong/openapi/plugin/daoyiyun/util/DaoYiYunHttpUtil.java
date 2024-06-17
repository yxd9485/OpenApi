package com.fenbeitong.openapi.plugin.daoyiyun.util;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunBaseRespDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunTokenService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求工具封装
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunHttpUtil {

    @Autowired
    private DaoYiYunTokenService tokenService;

    /**
     * 封装get请求，处理token
     * @param url
     * @param param
     * @param applicationId
     * @return
     */
    public String get(String url, Map<String, Object> param, String applicationId) {
        String accessToken = tokenService.getAccessToken(applicationId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/json");
        httpHeaders.add("X-Auth0-Token", accessToken);
        String result = RestHttpUtils.get(url, httpHeaders, param);
        DaoYiYunBaseRespDTO respDTO = JsonUtils.toObj(result, DaoYiYunBaseRespDTO.class);
        if (respDTO.getCode() == -401) {
            tokenService.clearTenantAccessToken(applicationId);
            return get(url, param, applicationId);
        }
        if (respDTO.getCode() != 0) {
            throw new OpenApiArgumentException(respDTO.getMsg());
        }
        return result;
    }

    /**
     * 封装post请求，处理token
     * @param url
     * @param jsonBody
     * @param applicationId
     * @return
     */
    public String post(String url, String jsonBody, String applicationId) {
        String accessToken = tokenService.getAccessToken(applicationId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/json");
        httpHeaders.add("X-Auth0-Token", accessToken);
        String result = RestHttpUtils.postJson(url, httpHeaders, jsonBody);
        DaoYiYunBaseRespDTO respDTO = JsonUtils.toObj(result, DaoYiYunBaseRespDTO.class);
        if (respDTO.getCode() == -401) {
            tokenService.clearTenantAccessToken(applicationId);
            return post(url, jsonBody, applicationId);
        }
        if (respDTO.getCode() != 0) {
            throw new OpenApiArgumentException(respDTO.getMsg());
        }
        return result;
    }

}
