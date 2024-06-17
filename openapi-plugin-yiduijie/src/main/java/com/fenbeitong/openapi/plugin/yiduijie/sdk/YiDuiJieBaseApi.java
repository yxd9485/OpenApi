package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Title: YiDuiJieBaseApi</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:40 PM
 */
@Slf4j
@Component
public class YiDuiJieBaseApi {

    @Autowired
    protected YiDuiJieRouter yiDuijieRouter;

    @Autowired
    protected RestHttpUtils httpUtils;

    protected String postJson(String url, String token, String jsonData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        return httpUtils.postJson(url, httpHeaders, jsonData);

    }

    protected String postText(String url, String token, String body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "text/plain; charset=UTF-8");
        httpHeaders.add("Authorization", token);
        return httpUtils.postBody(url, httpHeaders, body);
    }

    protected String get(String url, String token, Map<String, Object> param) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        return httpUtils.get(url, httpHeaders, param);
    }
}
