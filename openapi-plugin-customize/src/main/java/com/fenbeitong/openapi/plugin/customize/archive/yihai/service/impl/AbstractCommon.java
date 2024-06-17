package com.fenbeitong.openapi.plugin.customize.archive.yihai.service.impl;

import com.fenbeitong.openapi.plugin.customize.archive.yihai.dto.YiHaiConfigDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.util.Base64;
import java.util.Map;

/**
 * <p>Title: AbstractCommon</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-05-17 17:27
 */
@Slf4j
public abstract class AbstractCommon {
    public String getData(YiHaiConfigDTO yiHaiConfigDTO,Map params ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((yiHaiConfigDTO.getUserName() + ":" + yiHaiConfigDTO.getPassWord()).getBytes()));
        log.info("颐海入参：url:{},head:{},params:{}", yiHaiConfigDTO.getUrl(), headers, JsonUtils.toJson(params).toUpperCase());
        String result = RestHttpUtils.postJson(yiHaiConfigDTO.getUrl(), headers, JsonUtils.toJson(params).toUpperCase());
        log.info("颐海档案项目返回数据:{}", JsonUtils.toJson(result));
        return result;
    }
}
