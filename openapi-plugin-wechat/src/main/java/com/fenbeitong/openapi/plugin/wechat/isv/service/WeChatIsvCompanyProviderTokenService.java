package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatRedisKeyConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvProviderTokenRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvProviderTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_ERROR;
import static com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant.PROVIDER_TOKEN_KEY;

/**
 * 企业换取授权token
 * Created by log.chang on 2020/3/23.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvCompanyProviderTokenService {

    private final static String GET_PROVIDER_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";

    @Value("${wechat.isv.corp-id}")
    private String corpId;
    @Value("${wechat.isv.provider-secret}")
    private String providerSecret;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public String getProviderToken() {
        // 先尝试从redis查询
        String providerAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_ISV_PROVIDER_ACCESS_TOKEN);
        String providerAccessToken = (String) redisTemplate.opsForValue().get(providerAccessTokenKey);
        if (!StringUtils.isBlank(providerAccessToken)) {
            return providerAccessToken;
        }
        // 未命中缓存， 重新请求
        WeChatIsvProviderTokenRequest req = WeChatIsvProviderTokenRequest.builder().corpId(corpId).providerSecret(providerSecret).build();
        String resJson = httpUtil.postJson(GET_PROVIDER_TOKEN_URL, JsonUtils.toJson(req));
        WeChatIsvProviderTokenResponse res = JsonUtils.toObj(resJson, WeChatIsvProviderTokenResponse.class);
        if (res == null || StringUtils.isBlank(res.getProviderAccessToken())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_ERROR));
        }
        log.info("getProviderToken res is {}", res);
        providerAccessToken = res.getProviderAccessToken();
        Integer expiresIn = res.getExpiresIn();
        // 缓存redis
        redisTemplate.opsForValue().set(providerAccessTokenKey, providerAccessToken);
        // 有效期7200秒，设置7000秒过期，防止不可用
        redisTemplate.expire(providerAccessToken, expiresIn, TimeUnit.SECONDS);
        return providerAccessToken;
    }

    public void clearProviderToken() {
        String providerAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_ISV_PROVIDER_ACCESS_TOKEN);
        redisTemplate.delete(providerAccessTokenKey);
    }
}
