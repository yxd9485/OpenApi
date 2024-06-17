package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.finhub.auth.annotation.FinhubRequiredAuth;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResultEntity;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvWebService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl.DingtalkIsvWebServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * jsapi
 */
@RestController
@Slf4j
@RequestMapping("/dingtalk/isv/web")
public class DingtalkIsvWebController {

    @Autowired
    private IDingtalkIsvWebService dingtalkIsvWebService;

    @FinhubRequiredAuth
    @RequestMapping("/getJsapiSign")
    public Object getJsapiSign(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        DingtalkJsapiSignRespDTO dingtalkJsapiSignRespDTO = dingtalkIsvWebService.getJsapiSign(userInfo, data);
        return DingtalkResponseUtils.success(dingtalkJsapiSignRespDTO);
    }

    @RequestMapping("/get-user-info")
    public Object getUserInfo(HttpServletRequest request, HttpServletResponse response , @RequestBody String authCode) {
        Map<String, Object> map = JsonUtils.toObj(authCode, Map.class);
        String code = StringUtils.obj2str(map.get("authCode"));
        if(StringUtils.isBlank( code )){
            return DingtalkResponseUtils.error( -9999 , "缺少参数authCode！" );
        }
        DingtalkResultEntity userInfo = dingtalkIsvWebService.getUserInfo( code );
        return userInfo;
    }

    @RequestMapping("/update-user-phone")
    public Object updateUserPhone(HttpServletRequest request, HttpServletResponse response , @RequestBody String data) {
        String token = request.getHeader("X-Auth-Token");
        Map<String, Object> map = JsonUtils.toObj(data, Map.class);
        String unionId = StringUtils.obj2str(map.get("unionId"));
        if(StringUtils.isBlank( unionId )){
            return DingtalkResponseUtils.error( -9999 , "缺少参数userId！" );
        }
        return dingtalkIsvWebService.updateMobile( token ,  unionId) ;
    }

}
