package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseCode;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.constant.FxkRedisKeyConstant;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception.OpenApiFxkException;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkCorpAppService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: FxkAccessTokenServiceImpl</p>
 * <p>Description: 纷享销客获取token服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 4:28 PM
 */
@ServiceAspect
@Service
@Slf4j
public class FxkAccessTokenServiceImpl implements IFxkAccessTokenService {

    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IFxkCorpAppService fxkCorpAppService;

    @Override
    public FxkGetCorpAccessTokenRespDTO getCorpAccessToken(FxkGetCorpAccessTokenReqDTO req) {
        String result = RestHttpUtils.postJson(fxiaokeHost + "/cgi/corpAccessToken/get/V2", JsonUtils.toJson(req));
        FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO = JsonUtils.toObj(result, FxkGetCorpAccessTokenRespDTO.class);
        if (!fxkGetCorpAccessTokenRespDTO.getErrorCode().equals(0)) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_GET_CORP_ACCESS_TOKEN_FAILED,result);
        }
        return fxkGetCorpAccessTokenRespDTO;
    }


    /**
     * 获取应用token
     *
     * @param corpId
     * @return
     */
    @Override
    public String getFxkCorpAccessTokenByCorpId(String corpId) {
        // 先尝试从redis查询
        String corpAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FxkRedisKeyConstant.CORP_ACCESS_TOKEN, corpId));
        String corpAccessToken = (String) redisTemplate.opsForValue().get(corpAccessTokenKey);
        if (!StringUtils.isBlank(corpAccessToken)) {
            return corpAccessToken;
        }
        // redis未命中， 重新获取
        FxiaokeCorpApp fxiaokeCorpApp = fxkCorpAppService.getFxkCorpAppByCorpId(corpId);
        if (fxiaokeCorpApp == null) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_CORP_UN_REGIST);
        }
        FxkGetCorpAccessTokenReqDTO fxkGetCorpAccessTokenReqDTO = new FxkGetCorpAccessTokenReqDTO();
        fxkGetCorpAccessTokenReqDTO.setAppId(fxiaokeCorpApp.getAppId());
        fxkGetCorpAccessTokenReqDTO.setAppSecret(fxiaokeCorpApp.getAppSecret());
        fxkGetCorpAccessTokenReqDTO.setPermanentCode(fxiaokeCorpApp.getPermanent());
        FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO = getCorpAccessToken(fxkGetCorpAccessTokenReqDTO);
        if (fxkGetCorpAccessTokenRespDTO == null || fxkGetCorpAccessTokenRespDTO.getErrorCode() != 0) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_GET_CORP_ACCESS_TOKEN_FAILED);
        }
        corpAccessToken = fxkGetCorpAccessTokenRespDTO.getCorpAccessToken();
        // 缓存redis
        log.info("fxk save corpAccessToken,key={},value={}", corpAccessTokenKey, corpAccessToken);
        redisTemplate.opsForValue().set(corpAccessTokenKey, corpAccessToken);
        int expiresIn = fxkGetCorpAccessTokenRespDTO.getExpiresIn() - 200;
        redisTemplate.expire(corpAccessTokenKey, expiresIn, TimeUnit.SECONDS);
        return corpAccessToken;
    }

    @Override
    public void clearCorpAccaessTokenByCorpId(String corpId) {
        String corpAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FxkRedisKeyConstant.CORP_ACCESS_TOKEN, corpId));
        redisTemplate.delete(corpAccessTokenKey);
    }


}
