package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgListResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountResp;
import com.fenbeitong.openapi.plugin.seeyon.enums.ApiStatusCodeEnum;
import com.fenbeitong.openapi.plugin.seeyon.exceptions.SeeyonApiException;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonAccountService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ServiceAspect
@Service
public class SeeyonAccountServiceImpl implements SeeyonAccountService {

    @Autowired
    RestHttpUtils restHttpUtils;
    @Value("${seeyon.rest-apis.get-account-id}")
    private String seeyonAccountIdUrl;
    @Value("${seeyon.rest-apis.get-account-code}")
    private String seeyonAccountCodeUrl;
    @Value("${seeyon.rest-apis.get-organization}")
    private String seeyonOrgUrl;
    @Value("${seeyon.rest-apis.get-account-info}")
    private String seeyonOrgAccountUrl;

    @Override
    public String getAccountId(SeeyonAccountParam accountParam, String accountIdUrl, Map<String, String> tokenHeader) {
        boolean end = false;
        Integer counter = 0;
        SeeyonAccountResp seeyonAccountResp = null;
        while (!end) {
            try {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
                Map<String,Object> map = JsonUtils.toObj(JsonUtils.toJson(accountParam), Map.class);
                String accountUrl = accountIdUrl+seeyonAccountIdUrl+accountParam.getOrgName();
                String result = restHttpUtils.get(accountUrl, httpHeaders, Maps.newHashMap());
                seeyonAccountResp = JsonUtils.toObj(result, SeeyonAccountResp.class);
                if (Objects.isNull(seeyonAccountResp)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                end = true;
            } catch (SeeyonApiException ex) {
                if (counter > SeeyonConstant.RETRY_COUNTER) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                ++counter;
                try {
                    Thread.sleep(SeeyonConstant.RETRY_SLEEP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
            }
        }
        return String.valueOf(seeyonAccountResp.getOrgAccountId());
    }

    @Override
    public String getAccountCode(SeeyonAccountParam accountParam, String accountCodeUrl, Map<String, String> tokenHeader) {
        boolean end = false;
        Integer counter = 0;
        SeeyonAccountResp seeyonAccountResp = null;
        while (!end) {
            try {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
                Map<String,Object> map = JsonUtils.toObj(JsonUtils.toJson(accountParam), Map.class);
                String accountUrl = accountCodeUrl+seeyonAccountCodeUrl+accountParam.getAccountCode();
                String result = restHttpUtils.get(accountUrl, httpHeaders, Maps.newHashMap());
              List<SeeyonAccountResp>  seeyonAccountResps = JsonUtils.toObj(result, new TypeReference<List<SeeyonAccountResp>>() {
              });
                if (Objects.isNull(seeyonAccountResps)) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                seeyonAccountResp = seeyonAccountResps.get(0);
                end = true;
            } catch (SeeyonApiException ex) {
                if (counter > SeeyonConstant.RETRY_COUNTER) {
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
                ++counter;
                try {
                    Thread.sleep(SeeyonConstant.RETRY_SLEEP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SeeyonApiException(ApiStatusCodeEnum.SEEYON_ACCOUNT_ID_FAILED.transform());
                }
            }
        }
        return String.valueOf(seeyonAccountResp.getOrgAccountId());
    }

    @Override
    public List<SeeyonAccountOrgListResp> getOrgAccounts(String orgAccountsUrl, Map<String, String> tokenHeader) {
        boolean end = false;
        Integer counter = 0;
        SeeyonAccountResp seeyonAccountResp = null;
        List<SeeyonAccountOrgListResp> seeyonAccountResps = null;
        while (!end) {
            try {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
                String orgAccountUrl = orgAccountsUrl+seeyonOrgAccountUrl;
                String result = restHttpUtils.get(orgAccountUrl, httpHeaders, Maps.newHashMap());
                seeyonAccountResps = JsonUtils.toObj(result, new TypeReference<List<SeeyonAccountOrgListResp>>() {
                });
                if (Objects.isNull(seeyonAccountResps)) {
                    log.info("获取 Seeyon 单位列表为空 ");
                }
                end = true;
            } catch (Exception ex) {
                log.info("获取 Seeyon 单位列表失败 : {}",ex.getMessage());
            }
        }
        return seeyonAccountResps;
    }

    public SeeyonAccountResp getAccountInfoByName(SeeyonAccountParam accountParam, String accountIdUrl, Map<String, String> tokenHeader) {
        boolean end = false;
        Integer counter = 0;
        SeeyonAccountResp seeyonAccountResp = null;
        while (!end) {
            try {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(SeeyonConstant.TOKEN_HEADER, tokenHeader.get(SeeyonConstant.TOKEN_HEADER));
                Map<String,Object> map = JsonUtils.toObj(JsonUtils.toJson(accountParam), Map.class);
                String accountUrl = accountIdUrl+seeyonAccountIdUrl+accountParam.getOrgName();
                String result = restHttpUtils.get(accountUrl, httpHeaders, Maps.newHashMap());
                seeyonAccountResp = JsonUtils.toObj(result, SeeyonAccountResp.class);
                if (Objects.isNull(seeyonAccountResp)) {
                    log.info("获取Seeyon Account Id 为空");
                }
                end = true;
            } catch (Exception ex) {
                log.info("获取Seeyon Account Id 失败 : {}",ex.getMessage());
            }
        }
        return seeyonAccountResp;
    }

}
