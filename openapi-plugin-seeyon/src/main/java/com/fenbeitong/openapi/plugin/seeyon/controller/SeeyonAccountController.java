package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonAccountService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonAccessTokenService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonClientService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Controller
@RequestMapping("/seeyon/account")
public class SeeyonAccountController {

    @Autowired
    SeeyonClientService seeyonClientService;
    @Autowired
    SeeyonAccountService seeyonAccountService;
    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;

    /**
     * 获取致远OA公司账户id
     * @param orgName
     * @return
     * @throws UnsupportedEncodingException
     */
    public Object getSeeyonAccountId(String orgName) throws UnsupportedEncodingException {
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        String accessToken = seeyonAccessTokenService.getAccessToken(orgName);
        SeeyonAccountParam build = SeeyonAccountParam.builder().orgName(URLEncoder.encode(orgName, "UTF-8")).build();
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put(SeeyonConstant.TOKEN_HEADER, accessToken);
        String accountId = seeyonAccountService.getAccountId(build, seeyonClientByName.getSeeyonSysUri(), paramMap);
        return SeeyonResponseUtils.success(accountId);
    }

}
