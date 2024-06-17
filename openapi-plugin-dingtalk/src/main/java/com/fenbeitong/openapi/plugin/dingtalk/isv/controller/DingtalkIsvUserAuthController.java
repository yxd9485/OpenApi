package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvAuthRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvWebLoginInfoRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvUserAuthService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/20
 */
@Controller
@Slf4j
@RequestMapping("/dingtalk/isv/auth")
public class DingtalkIsvUserAuthController {

    @Autowired
    private IDingtalkIsvUserAuthService dingtalkIsvUserAuthService;

    @RequestMapping()
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(required = true) String code, @RequestParam(required = true) String corpId) throws Exception {
        DingtalkIsvAuthRespDTO authInfo = dingtalkIsvUserAuthService.appAuth(code, corpId);
        return DingtalkResponseUtils.success(authInfo);
    }

    @RequestMapping("/webauthcodelogin")
    @ResponseBody
    public Object webLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws Exception {
        DingtalkIsvWebLoginInfoRespDTO dingtalkIsvWebLoginInfoRespDTO = dingtalkIsvUserAuthService.webLogin(code);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", dingtalkIsvWebLoginInfoRespDTO);
        return JsonUtils.toJson(result);
    }

}
