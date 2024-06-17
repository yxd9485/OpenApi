package com.fenbeitong.openapi.plugin.welink.isv.util;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * Created by lizhen on 2020/3/22.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvHttpUtils {

    @Autowired
    private WeLinkIsvCompanyAuthService weLinkIsvCompanyAuthService;

    @Autowired
    private RestHttpUtils httpUtil;

    /**
     * Header拼接accessToken请求，失效重试。
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithAccessToken(String url, String jsonData, String tenantId) {
        String accessToken = weLinkIsvCompanyAuthService.getAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-wlk-Authorization", accessToken);
        String res = httpUtil.postJson(url, httpHeaders, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 47101 || errcode == 47103 || errcode == 501)) {
            log.info("postJsonWithAccessToken, accessToken失效, 重试");
            weLinkIsvCompanyAuthService.clearAccessToken(tenantId);
            res = postJsonWithAccessToken(url, jsonData, tenantId);
        }
        return res;
    }

    /**
     * get请求，url使用占位符参数
     * 使用map传参，失效重试。
     * 注意url只拼接到传参前，使用map传参
     *
     * @param url
     * @param param
     * @return
     */
    public String getJsonWithAccessToken(String url, Map<String, Object> param, String tenantId) {
        String accessToken = weLinkIsvCompanyAuthService.getAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-wlk-Authorization", accessToken);
        String res = httpUtil.get(url, httpHeaders, param);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 47101 || errcode == 47103 || errcode == 501)) {
            log.info("getJsonWithAccessToken, accessToken失效, 重试");
            weLinkIsvCompanyAuthService.clearAccessToken(tenantId);
            res = getJsonWithAccessToken(url, param, tenantId);
        }
        return res;
    }

}
