package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.finhub.auth.annotation.FinhubRequiredAuth;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvOpenPayService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 钉钉充值购买
 *
 * @author lizhen
 * @date 2020/11/6
 */
@RestController
@Slf4j
@RequestMapping("/dingtalk/isv/openpay")
public class DingtalkIsvOpenPayController {


    @Autowired
    private IDingtalkIsvOpenPayService dingtalkIsvOpenPayService;

    @RequestMapping("/recharge")
    @ResponseBody
    @FinhubRequiredAuth
    public Object recharge(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false, name = "callback_page") String callbackPage) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        String redirctUrl = dingtalkIsvOpenPayService.recharge(userInfo, callbackPage);
        HashMap<Object, Object> objectObjectHashMap = Maps.newHashMap();
        objectObjectHashMap.put("redirct_url", redirctUrl);
        return DingtalkResponseUtils.success(objectObjectHashMap);
    }
}
