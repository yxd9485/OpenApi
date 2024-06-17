package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.finhub.auth.annotation.FinhubRequiredAuth;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaUserAuthService;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiajsapiService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户授权免登
 *
 * @author lizhen
 * @date 2021/1/25
 */
@Controller
@Slf4j
@RequestMapping("/feishu/eia/auth")
public class FeiShuEiaUserAuthController {

    @Autowired
    private FeiShuEiaUserAuthService feiShuEiaUserAuthService;

    @Autowired
    private FeiShuEiajsapiService feiShuEiajsapiService;

    @RequestMapping()
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String code, @RequestParam String appId) throws Exception {
        LoginResVO authInfo = feiShuEiaUserAuthService.auth(code, appId);
        return FeiShuResponseUtils.success(authInfo);
    }

    //获取签名
    @RequestMapping("/getJsapiSign")
    @ResponseBody
    @FinhubRequiredAuth
    public Object getJsapiSign(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        FeishuJsapiSignRespDTO jsapiSign = feiShuEiajsapiService.getJsapiSign(userInfo, data);
        return FeiShuResponseUtils.success(jsapiSign);
    }

}
