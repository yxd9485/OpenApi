package com.fenbeitong.openapi.plugin.yunzhijia.utils;/**
 * <p>Title: YunzhijiaHttpUtils</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/4/30 2:50 下午
 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

/**
 * Created by lizhen on 2021/4/30.
 */
@ServiceAspect
@Service
@Slf4j
public class YunzhijiaHttpUtils {

    @Autowired
    private YunzhijiaTokenService yunzhijiaTokenService;

    /**
     * post请求， 使用appAccessToken
     *
     * @param url
     * @param jsonData
     * @param corpId
     * @return
     */
    public YunzhijiaResponse postWithAppAccessToken(String url, String jsonData, String corpId, Class clazz) {
        String accessToken = yunzhijiaTokenService.getYunzhijiaAppAccessToken(corpId);
        String newUrl = url + "?accessToken=" + accessToken;
        String res = RestHttpUtils.postJson(newUrl, jsonData);
        YunzhijiaResponse yunzhijiaAccessTokenRespDTO = JsonUtils.toObj(res, YunzhijiaResponse.class);
        if (yunzhijiaAccessTokenRespDTO.getErrorCode() == 10000400 || yunzhijiaAccessTokenRespDTO.getErrorCode() == 10000401) {
            yunzhijiaTokenService.cleanAppAccessToken(corpId);
            yunzhijiaAccessTokenRespDTO = postWithAppAccessToken(url, jsonData, corpId, clazz);
        }
        Object data = yunzhijiaAccessTokenRespDTO.getData();
        if (!ObjectUtils.isEmpty(data)) {
            yunzhijiaAccessTokenRespDTO.setData(JsonUtils.toObj(JsonUtils.toJson(data), clazz));
        }
        return yunzhijiaAccessTokenRespDTO;
    }
}
