package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccessTokenReq;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonAccessTokenService;
import com.fenbeitong.openapi.plugin.seeyon.service.impl.SeeyonClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seeyon/token")
public class SeeyonAccessTokenController {

    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;
    @Autowired
    SeeyonClientService seeyonClientService;

    /**
     * 获取致远OAaccess_token
     * @param orgName
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public Object getSeeyonAccessToken(String orgName) {
        SeeyonClient seeyonClientByName = seeyonClientService.getSeeyonClientByName(orgName);
        String seeyonUsername = seeyonClientByName.getSeeyonUsername();
        String seeyonPassword = seeyonClientByName.getSeeyonPassword();
        SeeyonAccessTokenReq build = SeeyonAccessTokenReq.builder()
                .userName(seeyonUsername)
                .password(seeyonPassword)
                .build();
        String seeyonSysUri = seeyonClientByName.getSeeyonSysUri();
        String accessToken = seeyonAccessTokenService.getAccessToken(build, seeyonSysUri);
        return SeeyonResponseUtils.success(accessToken);
    }
}
