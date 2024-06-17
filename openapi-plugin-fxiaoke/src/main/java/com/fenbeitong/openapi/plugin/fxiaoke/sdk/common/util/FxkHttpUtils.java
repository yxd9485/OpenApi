package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.util;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FxkHttpUtils {

    @Autowired
    private IFxkAccessTokenService fxkAccessTokenService;

    /**
     * post
     *
     * @param url
     * @return
     */
    public String postJsonWithAccessToken(String url, Map<String, Object> data, String corpId) {
        String accessToken = fxkAccessTokenService.getFxkCorpAccessTokenByCorpId(corpId);
        if (data == null) {
            data = new HashMap<>();
        }
        data.put("corpAccessToken", accessToken);
        String res = RestHttpUtils.postJson(url, JsonUtils.toJson(data));
        JSONObject jsonObject = JSONObject.parseObject(res);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && (errcode == 20016 || errcode == 10006)) {
            log.info("postJsonWithAccessToken, accessToken失效, 重试");
            fxkAccessTokenService.clearCorpAccaessTokenByCorpId(corpId);
            res = postJsonWithAccessToken(url, data, corpId);
        }
        return res;
    }


}
