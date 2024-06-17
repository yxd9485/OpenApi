package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.fenbeitong.finhub.auth.annotation.FinhubRequiredAuth;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvAuthRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvWebLoginInfoRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvUserAuthSrvice;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvjsapiService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户授权免登
 *
 * @author lizhen
 * @date 2020/6/5
 */
@Controller
@Slf4j
@RequestMapping("/feishu/isv/auth")
public class FeiShuIsvUserAuthController {

    @Autowired
    private FeiShuIsvUserAuthSrvice feiShuIsvUserAuthSrvice;

    @Autowired
    private FeiShuIsvjsapiService feiShuIsvjsapiService;

    //小程序登陆
    @RequestMapping()
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String code) throws Exception {
        FeiShuIsvAuthRespDTO authInfo = feiShuIsvUserAuthSrvice.auth(code);
        return FeiShuResponseUtils.success(authInfo);
    }

    //H5登陆
    @RequestMapping("/webauthh5")
    @ResponseBody
    public Object authH5(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String code) throws Exception {
        FeiShuIsvAuthRespDTO authInfo = feiShuIsvUserAuthSrvice.authH5(code);
        return FeiShuResponseUtils.success(authInfo);
    }


    @RequestMapping("/webauthcodelogin")
    @ResponseBody
    public Object webLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws Exception {
        FeiShuIsvWebLoginInfoRespDTO feiShuIsvWebLoginInfoRespDTO = feiShuIsvUserAuthSrvice.webLogin(code);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", feiShuIsvWebLoginInfoRespDTO);
        return JsonUtils.toJson(result);
    }


    //获取签名
    @RequestMapping("/getJsapiSign")
    @ResponseBody
    @FinhubRequiredAuth
    public Object getJsapiSign(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        FeishuJsapiSignRespDTO jsapiSign = feiShuIsvjsapiService.getJsapiSign(userInfo, data);
        return FeiShuResponseUtils.success(jsapiSign);
    }


}
