package com.fenbeitong.openapi.plugin.seeyon.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.seeyon.service.ISeeyonAuthService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 致远授权接口
 * @Auther xiaohai
 * @Date 2022/09/27
 */
@RestController
@RequestMapping("/seeyon/auth")
@Slf4j
public class SeeyonAuthController {

    @Autowired
    private ISeeyonAuthService seeyonAuthService;

    @RequestMapping("/getUserInfo")
    public Object getUserInfo( @RequestParam("authCode") String authCode ){
        LoginResVO loginResVO = seeyonAuthService.getLoginInfo(authCode);
        return OpenapiResponseUtils.success(loginResVO);
    }
}
