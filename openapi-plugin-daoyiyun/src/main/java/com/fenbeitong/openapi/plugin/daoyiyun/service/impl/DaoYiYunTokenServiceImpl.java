package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.daoyiyun.constant.DaoYiYunConstant;
import com.fenbeitong.openapi.plugin.daoyiyun.dao.DaoyiyunCorpDao;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunTokenRespDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.entity.DaoyiyunCorp;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunTokenService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import com.luastar.swift.base.net.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 道一云token
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunTokenServiceImpl implements DaoYiYunTokenService {


    @Autowired
    private DaoyiyunCorpDao daoyiyunCorpDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String getAccessToken(String applicationId) {
        //先尝试从redis查询
        String accessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY,
            MessageFormat.format(DaoYiYunConstant.REDIS_KEY_TOKEN, applicationId));
        log.info("accessTokenKey is:{}", accessTokenKey);
        String token = (String) redisTemplate.opsForValue().get(accessTokenKey);
        if (!StringUtils.isBlank(token)) {
            return token;
        }
        //请求道一云接口获取
        Map<String, String> accessKey = getAccessKey(applicationId);
        token = getAccessToken(accessKey);
        //缓存
        redisTemplate.opsForValue().set(accessTokenKey, token, 7, TimeUnit.HOURS);
        return token;
    }

    @Override
    public void clearTenantAccessToken(String applicationId) {
        String tokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY,
            MessageFormat.format(DaoYiYunConstant.REDIS_KEY_TOKEN, applicationId));
        redisTemplate.delete(tokenKey);
    }

    /**
     * 获取accessKey
     *
     * @param applicationId
     * @return
     */
    private Map<String, String> getAccessKey(String applicationId) {
        DaoyiyunCorp corpConfigInfo = daoyiyunCorpDao.getByApplicationId(applicationId);
        String random = RandomUtils.bsonId();
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> param = new HashMap<>();
        param.put("timestamp", timeStamp);
        param.put("random", random);
        param.put("corpId", corpConfigInfo.getCorpId());
        param.put("secret", corpConfigInfo.getSecret());
        param.put("account", corpConfigInfo.getAccount());
        String result = httpClientGet(DaoYiYunConstant.DAO_YI_YUN_HOST + DaoYiYunConstant.URL_ACCESS_KEY, param);
        DaoYiYunTokenRespDTO tokenRespDTO = JsonUtils.toObj(result, DaoYiYunTokenRespDTO.class);
        if (tokenRespDTO == null || tokenRespDTO.getCode() != 0) {
            throw new OpenApiArgumentException("获取accessKey失败");
        }
        param.put("accessKey", tokenRespDTO.getData());
        return param;
    }



    /**
     * 从道一云获取token
     *
     * @param param
     * @return
     */
    private String getAccessToken(Map<String, String> param) {
        String result = httpClientGet(DaoYiYunConstant.DAO_YI_YUN_HOST + DaoYiYunConstant.URL_ACCESS_TOKEN, param);
        DaoYiYunTokenRespDTO tokenRespDTO = JsonUtils.toObj(result, DaoYiYunTokenRespDTO.class);
        if (tokenRespDTO == null || tokenRespDTO.getCode() != 0) {
            throw new OpenApiArgumentException("获取token失败");
        }
        return tokenRespDTO.getData();
    }

    private static String httpClientGet(String url, Map<String, String> param) {
        if (!ObjectUtils.isEmpty(param)) {
            StringBuilder sb = new StringBuilder(url);
            sb.append("?");
            param.forEach((k, v) -> {
                sb.append(k).append("=").append(v).append("&");
            });
            url = sb.substring(0, sb.length() - 1);
        }
        log.info("道一云请求数据:{}", url);
        String result = HttpClientUtils.get(url);
        log.info("道一云返回数据:{}", result);
        return result;
    }

    public static void main(String[] args) {
        Map<String, String> param = new HashMap<>();
        param.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        param.put("random", "random");
        param.put("corpId", "ww8b0210745dc9cb5f");
        param.put("secret", "c1830269a4e44dfb9219013e025e5255");
        param.put("account", "SH0113");
        String result = httpClientGet(DaoYiYunConstant.DAO_YI_YUN_HOST + DaoYiYunConstant.URL_ACCESS_KEY, param);
        System.out.println(result);
    }
}
