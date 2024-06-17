package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccessTokenReq;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccessTokenResp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.enums.ApiStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Objects;

@ServiceAspect
@Service
public class SeeyonAccessTokenService {
    @Autowired
    RestHttpUtils restHttpUtils;
    @Autowired
    SeeyonClientService seeyonClientService;
    @Value("${seeyon.rest-apis.post-token}")
    private String seeyonTokenUrl;

    /**
     * 获取致远OAaccess_token
     *
     * @param tokenParam
     * @param tokenUrl
     * @return
     */
    public String getAccessToken(SeeyonAccessTokenReq tokenParam, String tokenUrl) {
        boolean end = false;
        Integer counter = 0;
        String result = "";
        //获取token重试机制，重试此处大于约定次数进行异常提醒
        while (!end) {
            try {
                String url = tokenUrl+seeyonTokenUrl+"/"+tokenParam.getUserName()+"/"+tokenParam.getPassword();
                 result = restHttpUtils.get(url, Maps.newHashMap() );
//                seeyonAccessTokenResp = JsonUtils.toObj(result, SeeyonAccessTokenResp.class);
                if (StringUtils.isBlank(result)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_TOKEN_FAILED.transform());
                }
                end = true;
            } catch (SeeyonApiException ex) {
                if (counter > SeeyonConstant.RETRY_COUNTER) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_TOKEN_FAILED.transform());
                }
                ++counter;
                try {
                    Thread.sleep(SeeyonConstant.RETRY_SLEEP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_TOKEN_FAILED.transform());
                }
            }
        }
        return result;
    }

    /**
     * 根据组织名称查询公司token
     *
     * @param orgName
     * @return
     */
    public String getAccessToken(String orgName) {
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        String seeyonUsername = seeyonClientByName.getSeeyonUsername();
        String seeyonPassword = seeyonClientByName.getSeeyonPassword();
        String url = seeyonClientByName.getSeeyonSysUri()+seeyonTokenUrl+"/"+seeyonUsername+"/"+seeyonPassword;
        boolean end = false;
        Integer counter = 0;
        SeeyonAccessTokenResp seeyonAccessTokenResp = null;
        //获取token重试机制，重试此处大于约定次数进行异常提醒
        while (!end) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                headers.add("Accept","application/json");
                String result = restHttpUtils.get(url, headers,Maps.newHashMap() );
                seeyonAccessTokenResp = JsonUtils.toObj(result, SeeyonAccessTokenResp.class);
                if (StringUtils.isBlank(result)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_TOKEN_FAILED.transform());
                }
                end = true;
            } catch (SeeyonApiException ex) {
                if (counter > SeeyonConstant.RETRY_COUNTER) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_TOKEN_FAILED.transform());
                }
                ++counter;
                try {
                    Thread.sleep(SeeyonConstant.RETRY_SLEEP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_TOKEN_FAILED.transform());
                }
            }
        }
        return seeyonAccessTokenResp.getId();
    }

}
