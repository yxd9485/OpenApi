package com.fenbeitong.openapi.plugin.feishu.common.util;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.RestPatchHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * 飞书http工具类，用于自动填充accessToken和accessToken失效重试
 *
 * @author lizhen
 * @date 2020/3/22
 */
@Slf4j
abstract public class AbstractFeiShuHttpUtils {

    @Autowired
    private ExceptionRemind exceptionRemind;

    /**
     * param填充appAccessToken请求，失效重试。
     *
     * @param url
     * @param param
     * @return
     */
    public String postJsonWithAppAccessToken(String url, Map<String, Object> param) {
        String appAccessToken = getFeiShuCompanyAuthService().getAppAccessToken();
        param.put("app_access_token", appAccessToken);
        String res = postJson(url, null, JsonUtils.toJson(param));
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode.equals(99991664)) {
            log.info("【feishu】 postJsonWithAppAccessToken ,appAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearAppAccessToken();
            res = postJsonWithAppAccessToken(url, param);
        }
        return res;
    }

    /**
     * param填充appAccessToken请求，失效重试。(内部应用)
     *
     * @param url
     * @param param
     * @return
     */
    public String postJsonWithAppAccessToken(String url, Map<String, Object> param, String corpId) {
        String appAccessToken = getFeiShuCompanyAuthService().getAppAccessToken(corpId);
        param.put("app_access_token", appAccessToken);
        String res = postJson(url, null, JsonUtils.toJson(param));
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode.equals(99991664)) {
            log.info("【feishu】 postJsonWithAppAccessToken ,appAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearAppAccessToken(corpId);
            res = postJsonWithAppAccessToken(url, param, corpId);
        }
        return res;
    }
    /**
     * post，带Authorization头
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithAppAccessToken(String url, String jsonData) {
        String appAccessToken = getFeiShuCompanyAuthService().getAppAccessToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + appAccessToken);
        String res = postJson(url, httpHeaders, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode.equals(99991664)) {
            log.info("【feishu】 postJsonWithAppAccessToken ,appAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearAppAccessToken();
            res = postJsonWithAppAccessToken(url, jsonData);
        }
        return res;
    }

    /**
     * Header拼接tenantAccessToken请求，失效重试。post请求
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithTenantAccessToken(String url, String jsonData, String tenantId) {
        String tenantAccessToken = getFeiShuCompanyAuthService().getTenantAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tenantAccessToken);
        String res = postJson(url, httpHeaders, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode.equals(99991663) || errcode.equals(99991668))) {
            log.info("【feishu】postTenantJsonWithAccessToken, tenantAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearTenantAccessToken(tenantId);
            res = postJsonWithTenantAccessToken(url, jsonData, tenantId);
        }
        return res;
    }


    /**
     * Header拼接tenantAccessToken请求，失效重试。post请求
     *
     * @param url
     * @param jsonData
     * @return
     */
    public String postJsonWithToken(String url, String jsonData, String tenantAccessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tenantAccessToken);
        String res = postJson(url, httpHeaders, jsonData);
        return res;
    }


    /**
     * Header拼接tenantAccessToken请求，失效重试。get请求
     *
     * @param url
     * @param param
     * @param tenantId
     * @return
     */
    public String getWithTenantAccessToken(String url, Map<String, Object> param, String tenantId) {
        String tenantAccessToken = getFeiShuCompanyAuthService().getTenantAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tenantAccessToken);
        String res = get(url, httpHeaders, param);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(res);
        } catch (Exception e) {
            log.info("解析返回结果错误 {}",e.getCause());
        }
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode.equals(99991663) || errcode.equals(99991668))) {
            log.info("【feishu】getWithTenantAccessToken, tenantAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearTenantAccessToken(tenantId);
            res = getWithTenantAccessToken(url, param, tenantId);
        }
        return res;
    }
    /**
     * Header拼接tenantAccessToken请求，失效重试。delete请求
     *
     * @param url
     * @param tenantId
     * @return
     */
    public String deleteWithTenantAccessToken(String url,  String tenantId) {
        String tenantAccessToken = getFeiShuCompanyAuthService().getTenantAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tenantAccessToken);
        String res = delete( url , httpHeaders);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(res);
        } catch (Exception e) {
            log.info("解析返回结果错误 {}",e.getCause());
        }
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode.equals(99991663) || errcode.equals(99991668))) {
            log.info("【feishu】getWithTenantAccessToken, tenantAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearTenantAccessToken(tenantId);
            res = deleteWithTenantAccessToken(url , tenantId);
        }
        return res;
    }

    /**
     * Header拼接tenantAccessToken请求，失效重试。delete请求
     *
     * @param url
     * @param tenantId
     * @return
     */
    public String patchWithTenantAccessToken(String url, String jsonData, String tenantId ) {
        String tenantAccessToken = getFeiShuCompanyAuthService().getTenantAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tenantAccessToken);
        String res = patch(url, httpHeaders, jsonData);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(res);
        } catch (Exception e) {
            log.info("解析返回结果错误 {}",e.getCause());
        }
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode.equals(99991663) || errcode.equals(99991668))) {
            log.info("【feishu】getWithTenantAccessToken, tenantAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearTenantAccessToken(tenantId);
            res = deleteWithTenantAccessToken(url , tenantId);
        }
        return res;
    }

    /**
     * Header拼接tenantAccessToken请求，失效重试。postFile请求
     *
     * @param url
     * @param tenantId
     * @return
     */
    public String postFileWithTenantAccessToken(String url, MultiValueMap multiValueMap, String tenantId ) {
        String tenantAccessToken = getFeiShuCompanyAuthService().getTenantAccessTokenByCorpId(tenantId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tenantAccessToken);
        String res = RestHttpUtils.postFile( url, httpHeaders, multiValueMap );
        return res;
    }

    /**
     * 获取companyAuthService,由子类实现以实现不同应用类型下的实例
     *
     * @return
     */
    protected abstract AbstractFeiShuCompanyAuthService getFeiShuCompanyAuthService();

    /**
     *
     * @param url
     * @param param
     * @return
     */
    public String getWithAppAccessToken(String url, Map<String, Object> param) {
        String appAccessToken = getFeiShuCompanyAuthService().getAppAccessToken();
        param.put("app_access_token", appAccessToken);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + appAccessToken);
        String res = get(url, httpHeaders, param);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode.equals(99991664)) {
            log.info("【feishu】 getWithAppAccessToken ,appAccessToken失效, 重试");
            getFeiShuCompanyAuthService().clearAppAccessToken();
            res = getWithAppAccessToken(url, param);
        }
        return res;
    }

    public String postJson(String url, String jsonData) {
        return postJson(url, null, jsonData);
    }
    public String postJson(String url, HttpHeaders httpHeaders, String jsonData) {
        String res = null;
        for (int i = 0; i < 5; i++) {
            try {
                res = RestHttpUtils.postJson(url, httpHeaders, jsonData);
            } catch (ResourceAccessException e) {
                if (i == 4) {
                    log.info("请求飞书失败：", e);
                    throw e;
                }
                continue;
            }
            break;
        }
        return res;
    }


    public String get(String url, HttpHeaders httpHeaders, Map<String, Object> param) {
        String res = null;
        for (int i = 0; i < 5; i++) {
            try {
                res = RestHttpUtils.get(url, httpHeaders, param);
            } catch (ResourceAccessException e) {
                if (i == 4) {
                    log.info("请求飞书失败：", e);
                    throw e;
                }
                continue;
            }
            break;
        }
        return res;
    }


    public String delete(String url , HttpHeaders httpHeaders) {
        String res = null;
        for (int i = 0; i < 5; i++) {
            try {
                res = RestHttpUtils.delete(url, httpHeaders);
            } catch (ResourceAccessException e) {
                if (i == 4) {
                    log.info("请求飞书失败：", e);
                    throw e;
                }
                continue;
            }
            break;
        }
        return res;
    }

    public String patch(String url , HttpHeaders httpHeaders, String JsonParam) {
        String res = null;
        for (int i = 0; i < 5; i++) {
            try {
                res = RestPatchHttpUtils.patch(url, httpHeaders, JsonParam);
            } catch (ResourceAccessException e) {
                if (i == 4) {
                    log.info("请求飞书失败：", e);
                    throw e;
                }
                continue;
            }
            break;
        }
        return res;
    }
}
