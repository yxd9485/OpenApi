package com.fenbeitong.openapi.plugin.wechat.isv.controller;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatAuthRespDTO;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvWebLoginInfo;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信授权登录
 * Created by log.chang on 2020/3/2.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/isv/auth")
public class WeChatIsvUserController {

    @Autowired
    private WeChatIsvUserAuthService weChatIsvUserAuthService;

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @RequestMapping()
    @ResponseBody
    public String auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String code, @RequestParam String state) throws Exception {
        log.info("code={};state={}");
        WeChatAuthRespDTO authInfo = weChatIsvUserAuthService.auth(code);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", authInfo);
        //Map<String, Object> result = new HashMap<>();
        //Map<String, String> loginMap = new HashMap<>(2);
        //loginMap.put("companyId", "5747fbc10f0e60e0709d8d7d");
        //loginMap.put("thirdEmployeeId", "third_laugher");
        //result.put("code", 0);
        //result.put("type", 0);
        //result.put("data", loginMap);
        return JsonUtils.toJson(result);
    }

    @RequestMapping("web")
    @ResponseBody
    public String webAuth(@RequestParam String token) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", weChatIsvUserAuthService.webAuth(token));
        return JsonUtils.toJson(result);
    }

    @RequestMapping("webauthcodelogin")
    @ResponseBody
    public String webAuthCodeLogin(@RequestParam("auth_code") String authCode) throws UnsupportedEncodingException {
        WeChatIsvWebLoginInfo weChatIsvWebLoginInfo = weChatIsvUserAuthService.webAuthCodeLogin(authCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", weChatIsvWebLoginInfo);
        return JsonUtils.toJson(result);
    }

    @RequestMapping("/installAuth")
    @ResponseBody
    public String installAuth(@RequestParam("auth_code") String authCode) throws UnsupportedEncodingException {
        WeChatIsvWebLoginInfo weChatIsvWebLoginInfo = weChatIsvCompanyAuthService.installAuth(authCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", weChatIsvWebLoginInfo);
        return JsonUtils.toJson(result);
    }
}
