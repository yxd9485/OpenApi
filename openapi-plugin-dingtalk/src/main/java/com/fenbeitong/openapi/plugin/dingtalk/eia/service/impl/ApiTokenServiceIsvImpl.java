package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.CacheConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpAppService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.taobao.api.ApiException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.concurrent.TimeUnit;

/**
 * @author zhaokechun
 * @date 2018/12/18 18:35
 */
@Slf4j
@ServiceAspect
@Service
public class ApiTokenServiceIsvImpl implements IApiTokenService {

    @Autowired
    private IDingtalkCorpAppService dingtalkCorpAppService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    private static Cache<String, String> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(CacheConstant.DINGTALK_TOKEN_EXPIRED, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build();

    @Override
    public String getAccessToken(String corpId) {
        PluginCorpAppDefinition corpAppDefinition = dingtalkCorpAppService.getByCorpId(corpId);
        String tokenKey = StringUtils.formatString(CacheConstant.DINGTALK_TOKEN_KEY, corpId);
        String token = cache.getIfPresent(tokenKey);
        if (!StringUtils.isBlank(token)) {
            return token;
        }
        token = getTokenFromRemote(corpId, corpAppDefinition.getThirdAppKey(), corpAppDefinition.getThirdAppSecret());
        cache.put(tokenKey, token);
        return token;
    }

    @Override
    public void clearCorpAccessToken(String corpId) {
        String tokenKey = StringUtils.formatString(CacheConstant.DINGTALK_TOKEN_KEY, corpId);
        String token = cache.getIfPresent(tokenKey);
        if (!StringUtils.isBlank(token)) {
            cache.invalidate(tokenKey);
        }
    }

    /**
     * 获取授权企业的accessToken
     *
     * @param corpId    授权企业ID
     * @param appKey    appKey
     * @param appSecret appSecret
     * @return
     */
    @SneakyThrows
    private String getTokenFromRemote(String corpId, String appKey, String appSecret) {
        log.info("调用钉钉accessToken接口, 参数：corpId: {}, appKey: {}, appSecret: {}", corpId, appKey, appSecret);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DefaultDingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/service/get_corp_token");
        OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
        req.setAuthCorpid(corpId);
        try {
            OapiServiceGetCorpTokenResponse response = client.execute(req, appKey, appSecret, "suiteTicket");
            log.info("调用钉钉accessToken接口完成，返回结果：{}", response.getBody());
            if (response.isSuccess()) {
                return response.getAccessToken();
            } else {
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉accessToken接口异常,[message]:{}", e.getErrMsg());
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, e.getErrMsg());
        }
    }
}
