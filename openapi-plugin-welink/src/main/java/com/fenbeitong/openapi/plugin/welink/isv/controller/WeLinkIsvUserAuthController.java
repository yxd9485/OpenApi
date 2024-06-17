package com.fenbeitong.openapi.plugin.welink.isv.controller;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseUtils;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvAuthRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvWebLoginInfoRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户授权
 * Created by lizhen on 2020/4/16.
 */
@Controller
@Slf4j
@RequestMapping("/welink/isv/auth")
public class WeLinkIsvUserAuthController {
    @Autowired
    private WeLinkIsvUserAuthService weLinkIsvUserAuthService;

    @RequestMapping()
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String code) throws Exception {
        log.info("code={};state={}");
        WeLinkIsvAuthRespDTO authInfo = weLinkIsvUserAuthService.auth(code);
        //Map<String, String> loginMap = new HashMap<>(2);
        //loginMap.put("companyId", "5747fbc10f0e60e0709d8d7d");
        //loginMap.put("thirdEmployeeId", "third_laugher");
        return WeLinkResponseUtils.success(authInfo);
    }


    @RequestMapping("/webauthcodelogin")
    @ResponseBody
    public Object webLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
        WeLinkIsvWebLoginInfoRespDTO weLinkIsvWebLoginInfoRespDTO = weLinkIsvUserAuthService.webLogin(code, state);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("type", 0);
        result.put("data", weLinkIsvWebLoginInfoRespDTO);
        return JsonUtils.toJson(result);
    }

}
