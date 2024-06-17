package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/fxiaoke/token")
public class FxkAccessTokenController {
    @Autowired
    IFxkAccessTokenService fxkAccessTokenService;

    @ResponseBody
    @RequestMapping("/get")
    public Object getAccessToken(@RequestBody FxkGetCorpAccessTokenReqDTO reqDTO) {
        String corpId = "FSAID_1318f50";
        //appseret
        String appSecret = "ce8420ba39744a3485b4285eeb804ae3";
        //永久授权码，公司级别调用时使用 app级别调用无需使用
        String permantCode = "2C22DC604C7B230AAA36DD5973E87832";
        String apiName = "appr4W66U2QE87__crmappr";
        FxkGetCorpAccessTokenReqDTO build = FxkGetCorpAccessTokenReqDTO.builder()
                .appId(corpId)
                .appSecret(appSecret)
                .permanentCode(permantCode)
                .build();
        FxkGetCorpAccessTokenRespDTO corpAccessToken = fxkAccessTokenService.getCorpAccessToken(build);
        return FxkResponseUtils.success(corpAccessToken);
    }
}
