package com.fenbeitong.openapi.plugin.beisen.standard.service.third;

import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenConstant;
import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenResponseCode;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenReqBaseDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenResultBaseDTO;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.beisen.common.exception.OpenApiBeisenException;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: BeiSenApiGetToken</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/3/14 3:11 下午
 */
@Slf4j
public class BeiSenApiGetToken {
    public static final String OPEN_PLUGIN_BEISEN_REDIS_KEY = "open_plugin_beisen_redis_key:{0}";
    public static final int BEISEN_TRY_COUNT = 5;
    public static final String START_DATE = "1990-01-01 00:00:00";

    @Value("${beisen.host}")
    public String beisenBaseUrl;
    @Value("${beisen.token-url}")
    public String beisenTokenUrl;
    @Value("${beisen.org-list-url}")
    public String beisenOrgListUrl;
    @Value("${beisen.employee-list-url}")
    public String beisenEmployeeListUrl;
    @Value("${beisen.employee-job-list-url}")
    public String beisenEmployeeJobListUrl;
    @Value("${beisen.business-apply-list-url}")
    public String beisenBusinessApplyListUrl;
    @Value("${beisen.business-outward-apply-list-url}")
    public String beisenBusinessOutWardApplyListUrl;
    @Value("${beisen.business-object-data-url}")
    public String beisenObjectDataUrl;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    /**
     * 获取token的接口
     */
    public String getAccessToken(BeisenParamConfig config) {
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, config.getCompanyId());
        String accessToken = (String) redisTemplate.opsForValue().get(beisenTokenKey);
        if (!StringUtils.isBlank(accessToken)) {
            return accessToken;
        }
        String result;
        String getTokenUrl;
        if (config.getTokenUrlIsNew()) {
            getTokenUrl = BeiSenConstant.token_url_new;
            Map requestMap = new HashMap();
            requestMap.put("app_secret", config.getSecret());
            requestMap.put("app_key", config.getKey());
            requestMap.put("grant_type", config.getGrantType());
            result = RestHttpUtils.postFormUrlEncodeForStr(getTokenUrl, null, requestMap);
        } else {
            getTokenUrl = beisenBaseUrl.concat(beisenTokenUrl);
            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("app_id", config.getAppId());
            requestMap.add("secret", config.getSecret());
            requestMap.add("tenant_id", config.getTenantId());
            requestMap.add("grant_type", config.getGrantType());
            result = RestHttpUtils.postForm(getTokenUrl, requestMap);
        }
        return token(result, beisenTokenKey);
    }

    /**
     * 获取北森访问接口token（新版）
     *
     * @param beisenCorp 北森企业配置
     * @return
     */
    public String getNewToken(BeisenCorp beisenCorp){
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenCorp.getCompanyId());
        String accessToken = (String) redisTemplate.opsForValue().get(beisenTokenKey);
        if (!StringUtils.isBlank(accessToken)) {
            return accessToken;
        }
        Map requestMap = new HashMap();
        requestMap.put("app_secret", beisenCorp.getAppSecret());
        requestMap.put("app_key", beisenCorp.getAppKey());
        requestMap.put("grant_type", "client_credentials");
        String result = RestHttpUtils.postFormUrlEncodeForStr(BeiSenConstant.token_url_new, null, requestMap);
        return token(result,beisenTokenKey);
    }

    private String token(String result, String beisenTokenKey) {
        Map<String, Object> resutlMap = new HashMap<>();
        int count = 1;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                if (!StringUtils.isBlank(result)) {
                    resutlMap = JsonUtils.toObj(result, Map.class);
                    if (resutlMap.get("access_token") != null) {
                        redisTemplate.opsForValue().set(beisenTokenKey, String.valueOf(resutlMap.get("access_token")), Long.valueOf(String.valueOf(resutlMap.get("expires_in"))) - 60, TimeUnit.SECONDS);
                        return String.valueOf(resutlMap.get("access_token"));
                    } else {
                        count++;
                    }
                }
            } catch (Exception e) {
                log.error("get beisen accessToken error", e);
                count++;
            }
        }
        return null;
    }

    /**
     * 获取接口权限，并请求北森接口。失败重试5次
     *
     * @param reqBaseDTO  请求参数
     * @param url url
     * @param beisenCorp 北森企业配置
     * @param tryCount 重试次数
     * @return 北森返回值基类
     */
    public BeisenResultBaseDTO postUrlWithTryCount(BeisenReqBaseDTO reqBaseDTO, String url, BeisenCorp beisenCorp, int tryCount) {
        if (tryCount >= 5) {
            log.warn("北森接口重试次数超过5次，url:{},参数:{}", url, reqBaseDTO);
            return null;
        }
        BeisenResultBaseDTO resultBaseDTO = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + getNewToken(beisenCorp));
            log.info("调用北森接口参数:{}", JsonUtils.toJson(reqBaseDTO));
            String result = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(reqBaseDTO));
            log.info("调用北森接口返回结果，result:{}", result);
            resultBaseDTO = JsonUtils.toObj(result, BeisenResultBaseDTO.class);
            if (ObjectUtils.isEmpty(resultBaseDTO) || !BeiSenConstant.BEISEN_RESULT_SUCCESS.equals(resultBaseDTO.getCode())) {
                tryCount++;
                log.info("北森接口开始第{}次重试",tryCount+1);
                postUrlWithTryCount(reqBaseDTO, url, beisenCorp, tryCount);
            }
        } catch (Exception e) {
            tryCount++;
            log.info("北森接口开始第{}次重试",tryCount+1);
            postUrlWithTryCount(reqBaseDTO, url, beisenCorp, tryCount);
        }
        return resultBaseDTO;
    }

    /**
     * 失败5次后抛出异常，接口调用成功才会有返回值
     *
     * @param reqBaseDTO  请求参数
     * @param url url
     * @param beisenCorp 北森企业配置
     * @return 接口结果
     */
    public BeisenResultBaseDTO postUrlWithToken(BeisenReqBaseDTO reqBaseDTO, String url, BeisenCorp beisenCorp) {
        BeisenResultBaseDTO resultBaseDTO = postUrlWithTryCount(reqBaseDTO, url, beisenCorp, 0);
        //失败重试5次resultBaseDTO才会null，后续可以加报警
        if (ObjectUtils.isEmpty(resultBaseDTO)){
            throw new OpenApiBeisenException(BeiSenResponseCode.BEISEN_TRY_COUNT_MAX_ERROR,"北森接口超过最大重试次数");
        }
        return resultBaseDTO;
    }

}
