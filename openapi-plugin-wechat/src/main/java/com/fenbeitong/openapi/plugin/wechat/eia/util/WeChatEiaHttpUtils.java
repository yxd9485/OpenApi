package com.fenbeitong.openapi.plugin.wechat.eia.util;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.service.company.WeChatEiaCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.beetl.ext.simulate.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lizhen
 * @date 2020/11/12
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaHttpUtils {


    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private WeChatEiaCompanyAuthService weChatEiaCompanyAuthService;

    /**
     * @param url
     * @param param
     * @param corpId
     * @return
     */
    public String getWithAppAccessToken(String url, Map<String, Object> param, String corpId) {
        String appAccessToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
        if (param == null) {
            param = new HashMap<>();
        }
        param.put("access_token", appAccessToken);
        String res = RestHttpUtils.get(url, null, param);
        Map<String, Object> map = JsonUtils.toObj(res, Map.class);
        Integer errcode = NumericUtils.obj2int(map.get("errcode"));
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014 || errcode == 42009)) {
            log.info("getWithAppAccessToken, appAccessToken, 重试");
            wechatTokenService.clearWeChatAppTokenByCorpId(corpId);
            res = getWithAppAccessToken(url, param, corpId);
        }
        return res;
    }

    public String postJsonWithSuiteAccessToken(String url, String jsonData) {
        String suiteAccessToken = weChatEiaCompanyAuthService.getSuiteAccessToken();
        String newUrl = url + suiteAccessToken;
        String res = RestHttpUtils.postJson(newUrl, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 40082 || errcode == 42001 || errcode == 40014 || errcode == 42009)) {
            log.info("postJsonWithSuiteAccessToken, suiteAccessToken失效, 重试");
            weChatEiaCompanyAuthService.clearSuiteAccessToken();
            res = postJsonWithSuiteAccessToken(url, jsonData);
        }
        return res;
    }

    /**
     *  根据appToken请求企业微信接口（post请求）
     * @param url 企业微信接口
     * @param jsonData requestBody
     * @param corpId 三方企业id
     * @return 返回结果
     */
    public String postJsonWithAppAccessToken(String url, String jsonData,String corpId) {
        String appAccessToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
        String newUrl = url + appAccessToken;
        String res = RestHttpUtils.postJson(newUrl, jsonData);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errCode = jsonObject.getInteger("errcode");
        // 42001：access_token已过期；40014：不合法的access_token；（40014会不会造成递归的死循环？）
        if (errCode != null && (errCode == 42001 || errCode == 40014 )) {
            log.info("postJsonWithAppAccessToken, appAccessToken失效, 重试");
            wechatTokenService.clearWeChatAppTokenByCorpId(corpId);
            res = postJsonWithAppAccessToken(url, jsonData,corpId);
        }
        return res;
    }

}
