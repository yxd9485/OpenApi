package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.finhub.auth.annotation.FinhubRequiredAuth;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaWebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jsapi
 */
@RestController
@Slf4j
@RequestMapping("/dingtalk/eia/web")
public class DingtalkEiaWebController {

    @Autowired
    private IDingtalkEiaWebService dingtalkEiaWebService;

    @FinhubRequiredAuth
    @RequestMapping("/getJsapiSign")
    public Object getJsapiSign(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        DingtalkJsapiSignRespDTO dingtalkJsapiSignRespDTO = dingtalkEiaWebService.getJsapiSign(userInfo, data);
        return DingtalkResponseUtils.success(dingtalkJsapiSignRespDTO);
    }

}
