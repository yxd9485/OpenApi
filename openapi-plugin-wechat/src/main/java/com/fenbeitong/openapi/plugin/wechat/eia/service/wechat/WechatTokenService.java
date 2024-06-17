package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.dao.WeChatSecretDao;
import com.fenbeitong.openapi.plugin.support.init.entity.WeChatSecret;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatToken;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatRedisKeyConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatApply;
import com.fenbeitong.openapi.plugin.wechat.eia.service.apply.WeChatEiaApprovolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: WechatTokenService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/26 3:50 PM
 */
@ServiceAspect
@Service
@Slf4j
public class WechatTokenService {

    @Autowired
    private PluginCallWeChatEiaService callWeChatService;

    @Autowired
    protected PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    @Autowired
    protected PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private WeChatEiaApprovolService weChatEiaApprovolService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WeChatSecretDao weChatSecretDao;

    /**
     * 获取通讯录token
     *
     * @param companyId
     * @return
     */
    public String getWeChatContactToken(String companyId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        String corpId = corpDefinition.getThirdCorpId();
        return getWeChatContactTokenByCorpId(corpId);
    }


    /**
     * 获取通讯录token
     *
     * @param corpId
     * @return
     */
    public String getWeChatContactTokenByCorpId(String corpId) {
        PluginCorpAppDefinition appPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        // 先尝试从redis查询
        String contactAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_EIA_CONTACT_ACCESS_TOKEN, corpId));
        String contactAccessToken = (String) redisTemplate.opsForValue().get(contactAccessTokenKey);
        if (!StringUtils.isBlank(contactAccessToken)) {
            return contactAccessToken;
        }
        // redis未命中， 重新获取
        WeChatToken weChatToken = callWeChatService.getWeChatCorpAccessToken(corpId, appPluginCorpApp.getThirdAppSecret());
        if (weChatToken == null || StringUtils.isBlank(weChatToken.getAccessToken())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ACCESS_TOKEN_IS_NULL));
        }
        contactAccessToken = weChatToken.getAccessToken();
        // 缓存redis
        log.info("wechat eia save contactAccessToken,key={},value={}", contactAccessTokenKey, contactAccessToken);
        redisTemplate.opsForValue().set(contactAccessTokenKey, contactAccessToken);
        int expiresIn = weChatToken.getExpiresIn() - 200;
        redisTemplate.expire(contactAccessTokenKey, expiresIn, TimeUnit.SECONDS);
        return contactAccessToken;
    }

    /**
     * 获取应用token
     *
     * @param companyId
     * @return
     */
    public String getWeChatAppToken(String companyId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        String corpId = corpDefinition.getThirdCorpId();
        return getWeChatAppTokenByCorpId(corpId);
    }

    /**
     * 获取应用token
     *
     * @param corpId
     * @return
     */
    public String getWeChatAppTokenByCorpId(String corpId) {
        PluginCorpAppDefinition appPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        if (ObjectUtils.isEmpty(appPluginCorpApp)) {
            log.warn("公司信息未配置 , 请检查 dingtalk_corp_app corpId:{}",corpId);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_INFO_NOT_EXIST));
        }
        // 先尝试从redis查询
        String appAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_EIA_APP_ACCESS_TOKEN, corpId));
        String appAccessToken = (String) redisTemplate.opsForValue().get(appAccessTokenKey);
        if (!StringUtils.isBlank(appAccessToken)) {
            return appAccessToken;
        }
        // redis未命中， 重新获取
        WeChatToken weChatToken = callWeChatService.getWeChatCorpAccessToken(corpId, appPluginCorpApp.getThirdAppKey());
        if (weChatToken == null || StringUtils.isBlank(weChatToken.getAccessToken())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ACCESS_TOKEN_IS_NULL));
        }
        appAccessToken = weChatToken.getAccessToken();
        // 缓存redis
        log.info("wechat eia save appAccessToken,key={},value={}", appAccessTokenKey, appAccessToken);
        redisTemplate.opsForValue().set(appAccessTokenKey, appAccessToken);
        int expiresIn = weChatToken.getExpiresIn() - 200;
        redisTemplate.expire(appAccessTokenKey, expiresIn, TimeUnit.SECONDS);
        return appAccessToken;
    }

    public void clearWeChatAppTokenByCorpId(String corpId) {
        String appAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_EIA_APP_ACCESS_TOKEN, corpId));
        redisTemplate.delete(appAccessTokenKey);
    }

    /**
     * 获取审批token
     *
     * @param companyId
     * @return
     */
    public String getWeChatApprovalToken(String companyId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        String corpId = corpDefinition.getThirdCorpId();
        return getWeChatApprovalTokenByCorpId(corpId);
    }

    /**
     * 获取审批token
     *
     * @param corpId
     * @return
     */
    public String getWeChatApprovalTokenByCorpId(String corpId) {
        WeChatApply weChatApply = weChatEiaApprovolService.getWeChatApplyInfoByCorpId(corpId);
        // 先尝试从redis查询
        String approvalAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_EIA_APPROVAL_ACCESS_TOKEN, corpId));
        String approvalAccessToken = (String) redisTemplate.opsForValue().get(approvalAccessTokenKey);
        if (!StringUtils.isBlank(approvalAccessToken)) {
            return approvalAccessToken;
        }
        // redis未命中， 重新获取
        WeChatToken weChatToken = callWeChatService.getWeChatCorpAccessToken(corpId, weChatApply.getAgentSecret());
        if (weChatToken == null || StringUtils.isBlank(weChatToken.getAccessToken())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ACCESS_TOKEN_IS_NULL));
        }
        approvalAccessToken = weChatToken.getAccessToken();
        // 缓存redis
        log.info("wechat eia save approvalAccessToken,key={},value={}", approvalAccessTokenKey, approvalAccessToken);
        redisTemplate.opsForValue().set(approvalAccessTokenKey, approvalAccessToken);
        int expiresIn = weChatToken.getExpiresIn() - 200;
        redisTemplate.expire(approvalAccessTokenKey, expiresIn, TimeUnit.SECONDS);
        return approvalAccessToken;
    }


    /**
     * 获取token new
     *
     * @return
     */
    public String getWeChatToken(String companyId, int agentType) {
        WeChatSecret weChatSecret = weChatSecretDao.getSecret(companyId, agentType);
        // 先尝试从redis查询
        String accessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_ACCESS_TOKEN, companyId));
        String accessToken = (String) redisTemplate.opsForValue().get(accessTokenKey);
        if (!StringUtils.isBlank(accessToken)) {
            return accessToken;
        }
        // redis未命中， 重新获取
        WeChatToken weChatToken = callWeChatService.getWeChatCorpAccessToken(weChatSecret.getCorpId(), weChatSecret.getAgentSecret());
        if (weChatToken == null || StringUtils.isBlank(weChatToken.getAccessToken())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ACCESS_TOKEN_IS_NULL));
        }
        accessToken = weChatToken.getAccessToken();
        // 缓存redis
        log.info("wechat eia save AccessToken,key={},value={}", accessTokenKey, accessToken);
        redisTemplate.opsForValue().set(accessTokenKey, accessToken);
        int expiresIn = weChatToken.getExpiresIn() - 200;
        redisTemplate.expire(accessTokenKey, expiresIn, TimeUnit.SECONDS);
        return accessToken;
    }
}


