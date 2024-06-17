package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuRedisKeyConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuAppAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuCompanyAuthService;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuAppAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuTenantAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * 飞书企业授权service
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaCompanyAuthService extends AbstractFeiShuCompanyAuthService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Autowired
    protected PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    /**
     * 获取第三方应用凭证
     *
     * @return
     */
    @Override
    public String getAppAccessToken() {
        return null;
    }

    /**
     * tenant_access_token
     *
     * @param corpId
     * @returnT
     */
    @Override
    public String getTenantAccessTokenByCorpId(String corpId) {
        // 先尝试从redis查询
        String tenantAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_EIA_TENANT_ACCESS_TOKEN, corpId));
        String tenantAccessToken = (String) redisTemplate.opsForValue().get(tenantAccessTokenKey);
        if (!StringUtils.isBlank(tenantAccessToken)) {
            return tenantAccessToken;
        }
        // 未命中缓存， 重新请求
        String getTenantAccessTokenUrl = feishuHost + FeiShuConstant.INTERNAL_TENANT_ACCESS_TOKEN_URL;
        PluginCorpAppDefinition appPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        if ( null == appPluginCorpApp ){
            log.warn("【feishu eia】 get company authInfo 失败 appPluginCorpApp : {}, corpId : {} ", appPluginCorpApp,corpId);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_EIA_COMPANY_UNDEFINED);
        }
        FeiShuAppAccessTokenReqDTO feiShuAppAccessTokenReqDTO = new FeiShuAppAccessTokenReqDTO();
        feiShuAppAccessTokenReqDTO.setAppId(appPluginCorpApp.getThirdAppKey());
        feiShuAppAccessTokenReqDTO.setAppSecret(appPluginCorpApp.getThirdAppSecret());
        String res = feiShuEiaHttpUtils.postJson(getTenantAccessTokenUrl, JsonUtils.toJson(feiShuAppAccessTokenReqDTO));
        FeiShuTenantAccessTokenRespDTO feiShuTenantAccessTokenRespDTO = JsonUtils.toObj(res, FeiShuTenantAccessTokenRespDTO.class);
        if (feiShuTenantAccessTokenRespDTO == null || StringUtils.isBlank(feiShuTenantAccessTokenRespDTO.getTenantAccessToken())) {
            log.warn("【feishu eia】 getTenantAccessTokenByCorpId失败:{}", res);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_TENANT_ACCESS_TOKEN_FAILED);
        }
        tenantAccessToken = feiShuTenantAccessTokenRespDTO.getTenantAccessToken();
        // 缓存redis
        log.info("【feishu eia】 saveTenantAccessToken,key={},value={}", tenantAccessTokenKey, tenantAccessToken);
        redisTemplate.opsForValue().set(tenantAccessTokenKey, tenantAccessToken);
        redisTemplate.expire(tenantAccessTokenKey, feiShuTenantAccessTokenRespDTO.getExpire(), TimeUnit.SECONDS);
        return tenantAccessToken;
    }

    /**
     * appAccessToken失效，清除redis重新获取
     */
    @Override
    public void clearAppAccessToken() {
    }

    /**
     * tenantAccessToken失效，清除redis重新获取
     *
     * @param corpId
     */
    @Override
    public void clearTenantAccessToken(String corpId) {
        String tenantAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_EIA_TENANT_ACCESS_TOKEN, corpId));
        redisTemplate.delete(tenantAccessTokenKey);
    }

    /**
     * appAccessToken失效，清除redis重新获取
     */
    @Override
    public void clearAppAccessToken(String corpId) {
        String appAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_EIA_APP_ACCESS_TOKEN, corpId));
        redisTemplate.delete(appAccessTokenKey);
    }

    /**
     * 在openAuthInfo表中不存在
     *
     * @param appId
     * @param appSecret
     * @returnT
     */
    @Override
    public String getTenantAccessTokenByAppIdAndSecret(String appId ,String appSecret) {
        // 先尝试从redis查询
        String tenantAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_EIA_TENANT_ACCESS_TOKEN, appId));
        String tenantAccessToken = (String) redisTemplate.opsForValue().get(tenantAccessTokenKey);
        if (!StringUtils.isBlank(tenantAccessToken)) {
            return tenantAccessToken;
        }
        // 未命中缓存， 重新请求
        String getTenantAccessTokenUrl = feishuHost + FeiShuConstant.INTERNAL_TENANT_ACCESS_TOKEN_URL;
        FeiShuAppAccessTokenReqDTO feiShuAppAccessTokenReqDTO = new FeiShuAppAccessTokenReqDTO();
        feiShuAppAccessTokenReqDTO.setAppId( appId );
        feiShuAppAccessTokenReqDTO.setAppSecret( appSecret );
        String res = feiShuEiaHttpUtils.postJson(getTenantAccessTokenUrl, JsonUtils.toJson(feiShuAppAccessTokenReqDTO));
        FeiShuTenantAccessTokenRespDTO feiShuTenantAccessTokenRespDTO = JsonUtils.toObj(res, FeiShuTenantAccessTokenRespDTO.class);
        if (feiShuTenantAccessTokenRespDTO == null || StringUtils.isBlank(feiShuTenantAccessTokenRespDTO.getTenantAccessToken())) {
            log.warn("【feishu eia】 getTenantAccessTokenByCorpId失败:{}", res);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_TENANT_ACCESS_TOKEN_FAILED);
        }
        tenantAccessToken = feiShuTenantAccessTokenRespDTO.getTenantAccessToken();
        // 缓存redis
        log.info("【feishu eia】 saveTenantAccessToken,key={},value={}", tenantAccessTokenKey, tenantAccessToken);
        redisTemplate.opsForValue().set(tenantAccessTokenKey, tenantAccessToken);
        redisTemplate.expire(tenantAccessTokenKey, feiShuTenantAccessTokenRespDTO.getExpire(), TimeUnit.SECONDS);
        return tenantAccessToken;
    }


    /**
     * 获取第三方应用凭证
     *
     * @return
     */
    @Override
    public String getAppAccessToken(String corpId) {
        // 先尝试从redis查询
        String appAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_EIA_APP_ACCESS_TOKEN, corpId));
        String appAccessToken = (String) redisTemplate.opsForValue().get(appAccessTokenKey);
        if (!StringUtils.isBlank(appAccessToken)) {
            return appAccessToken;
        }
        // 未命中缓存， 重新请求
        String getAppAccessTokenUrl = feishuHost + FeiShuConstant.INTERNAL_APP_ACCESS_TOKEN_URL;
        PluginCorpAppDefinition appPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        FeiShuAppAccessTokenReqDTO feiShuAppAccessTokenReqDTO = new FeiShuAppAccessTokenReqDTO();
        feiShuAppAccessTokenReqDTO.setAppId(appPluginCorpApp.getThirdAppKey());
        feiShuAppAccessTokenReqDTO.setAppSecret(appPluginCorpApp.getThirdAppSecret());
        String res = feiShuEiaHttpUtils.postJson(getAppAccessTokenUrl, JsonUtils.toJson(feiShuAppAccessTokenReqDTO));
        FeiShuAppAccessTokenRespDTO feiShuAppAccessTokenRespDTO = JsonUtils.toObj(res, FeiShuAppAccessTokenRespDTO.class);
        if (feiShuAppAccessTokenRespDTO == null || StringUtils.isBlank(feiShuAppAccessTokenRespDTO.getAppAccessToken())) {
            log.warn("【feishu eia】 getAppAccessToken:{}", res);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_APP_ACCESS_TOKEN_FAILED);
        }
        appAccessToken = feiShuAppAccessTokenRespDTO.getAppAccessToken();
        // 缓存redis
        log.info("【feishu eia】 saveAppAccessToken,key={},value={}", appAccessTokenKey, appAccessToken);
        redisTemplate.opsForValue().set(appAccessTokenKey, appAccessToken);
        redisTemplate.expire(appAccessTokenKey, feiShuAppAccessTokenRespDTO.getExpire(), TimeUnit.SECONDS);
        return appAccessToken;
    }


}
