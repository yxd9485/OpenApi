package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.constant.FxkConstant;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeAuthRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@EnableAutoConfiguration
@RequestMapping("/fxiaoke/auth")
public class FxkUserAuthController {

    @Autowired
    private IFxkUserAuthService fxkUserAuthService;

    @Value("${host.webapp}")
    private String webappHost;

    @RequestMapping()
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam String code, @RequestParam String appId, @RequestParam String state) {
        FxiaokeAuthRespDTO auth = fxkUserAuthService.auth(code, appId, state);
        return FxkResponseUtils.success(auth);
    }

}
