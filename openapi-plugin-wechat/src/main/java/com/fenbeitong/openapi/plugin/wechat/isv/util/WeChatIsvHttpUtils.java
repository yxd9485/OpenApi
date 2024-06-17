package com.fenbeitong.openapi.plugin.wechat.isv.util;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyProviderTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhen on 2020/3/22.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvHttpUtils {

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private WeChatIsvCompanyProviderTokenService weChatIsvCompanyProviderTokenService;

    /**
     * 末尾拼接suiteAccessToken请求，失效重试。
     * 注意url只拼接到"suite_access_token="即可
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithSuiteAccessToken(String url, String jsonData) {
        String suiteAccessToken = weChatIsvCompanyAuthService.getSuiteAccessToken();
        String newUrl = url + suiteAccessToken;
        String res = httpUtil.postJson(newUrl, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014 || errcode == 42009)) {
            log.info("postJsonWithSuiteAccessToken, suiteAccessToken失效, 重试");
            weChatIsvCompanyAuthService.clearSuiteAccessToken();
            res = postJsonWithSuiteAccessToken(url, jsonData);
        }
        return res;
    }

    /**
     * get请求，url使用占位符参数，如https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?suite_access_token={suite_access_token}
     * 使用map传参，失效重试。
     * 注意url只拼接到传参前，使用map传参
     *
     * @param url
     * @param param
     * @return
     */
    public String getJsonWithSuiteAccessToken(String url, Map<String, String> param) {
        String suiteAccessToken = weChatIsvCompanyAuthService.getSuiteAccessToken();
        param.put("suite_access_token", suiteAccessToken);
        String res = httpUtil.get(url, param);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014)) {
            log.info("getJsonWithSuiteAccessToken, suiteAccessToken失效, 重试");
            weChatIsvCompanyAuthService.clearSuiteAccessToken();
            res = getJsonWithSuiteAccessToken(url, param);
        }
        return res;
    }


    /**
     * 末尾拼接accessToken请求，失效重试。
     * 注意url只拼接到"access_token="即可
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithAccessToken(String url, String jsonData, String corpId) {
        String accessToken = weChatIsvCompanyAuthService.getAccessTokenByCorpId(corpId);
        String newUrl = url + accessToken;
        String res = httpUtil.postJson(newUrl, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014)) {
            log.info("postJsonWithAccessToken, accessToken失效, 重试");
            weChatIsvCompanyAuthService.clearAccessToken(corpId);
            res = postJsonWithAccessToken(url, jsonData, corpId);
        }
        return res;
    }

    /**
     * get请求，url使用占位符参数，如：https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token={access_token}
     * 使用map传参，失效重试。
     * 注意url只拼接到传参前，使用map传参
     *
     * @param url
     * @param param
     * @return
     */
    public String getJsonWithAccessToken(String url, Map<String, String> param, String corpId) {
        String accessToken = weChatIsvCompanyAuthService.getAccessTokenByCorpId(corpId);
        param.put("access_token", accessToken);
        String res = httpUtil.get(url, param);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014)) {
            log.info("getJsonWithAccessToken, accessToken失效, 重试");
            weChatIsvCompanyAuthService.clearAccessToken(corpId);
            res = getJsonWithAccessToken(url, param, corpId);
        }
        return res;
    }

    /**
     * 末尾拼接accessToken请求，失效重试。
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithProviderAccessToken(String url, String jsonData) {
        String providerAccessToken = weChatIsvCompanyProviderTokenService.getProviderToken();
        String newUrl = url + providerAccessToken;
        String res = httpUtil.postJson(newUrl, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014)) {
            log.info("postJsonWithProviderAccessToken, providerAccessToken失效, 重试");
            weChatIsvCompanyProviderTokenService.clearProviderToken();
            res = postJsonWithProviderAccessToken(url, jsonData);
        }
        return res;
    }

    /**
     * 末尾拼接accessToken请求，失效重试。
     *
     * @param url
     * @param file
     * @return
     */
    public String postFileWithProviderAccessToken(String url, File file) {
        String providerAccessToken = weChatIsvCompanyProviderTokenService.getProviderToken();
        String newUrl = url + providerAccessToken;
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("media", new FileSystemResource(file));
        String res = httpUtil.postFile(newUrl, multiValueMap);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014)) {
            log.info("postFileWithProviderAccessToken, providerAccessToken失效, 重试");
            weChatIsvCompanyProviderTokenService.clearProviderToken();
            res = postFileWithProviderAccessToken(url, file);
        }
        return res;
    }


    /**
     * get请求，参数里自动添加provider_access_token
     *
     * @param url
     * @param param
     * @return
     */
    public String getWithProviderAccessToken(String url, Map<String, Object> param) {
        String providerAccessToken = weChatIsvCompanyProviderTokenService.getProviderToken();
        if (ObjectUtils.isEmpty(param)) {
            param = new HashMap<>();
        }
        param.put("provider_access_token", providerAccessToken);
        String res = httpUtil.get(url, null, param);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014)) {
            log.info("getWithProviderAccessToken, providerAccessToken失效, 重试");
            weChatIsvCompanyProviderTokenService.clearProviderToken();
            res = getWithProviderAccessToken(url, param);
        }
        return res;
    }

}
