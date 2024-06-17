package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.constant.YiDuiJieConstant;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieTokenReq;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieTokenResp;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieTokenApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: YiDuiJieTokenServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:07 PM
 */
@ServiceAspect
@Service
public class YiDuiJieTokenServiceImpl implements IYiDuiJieTokenService {

    @Value("${yiduijie.username}")
    private String userName;

    @Value("${yiduijie.password}")
    private String password;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private YiDuiJieTokenApi yiDuiJieTokenClient;

    @Override
    public String getYiDuiJieToken() {
        String tokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, YiDuiJieConstant.YIDUIJIE_TOKEN_KEY);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String token = (String) valueOperations.get(tokenKey);
        if (ObjectUtils.isEmpty(token)) {
            YiDuiJieTokenResp yiDuijieTokenResp = yiDuiJieTokenClient.getToken(YiDuiJieTokenReq.builder().username(userName).password(password).build());
            if (yiDuijieTokenResp == null || !yiDuijieTokenResp.success()) {
                throw new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.GET_TOKEN_ERROR));
            }
            token = yiDuijieTokenResp.getTokenType() + " " + yiDuijieTokenResp.getAccessToken();
            //易对接token两小时 失效 这里处理为100分钟
            valueOperations.set(tokenKey, token, 100L, TimeUnit.MINUTES);
        }
        return token;
    }

    @Override
    public String getYiDuiJieToken(String userName, String password) {
        YiDuiJieTokenResp yiDuijieTokenResp = yiDuiJieTokenClient.getToken(YiDuiJieTokenReq.builder().username(userName).password(password).build());
        if (yiDuijieTokenResp == null || !yiDuijieTokenResp.success()) {
            throw new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.GET_TOKEN_ERROR));
        }
        return yiDuijieTokenResp.getTokenType() + " " + yiDuijieTokenResp.getAccessToken();
    }
}
