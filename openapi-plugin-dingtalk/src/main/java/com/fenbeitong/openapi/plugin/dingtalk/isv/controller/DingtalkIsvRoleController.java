package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.dingtalk.api.response.OapiRoleListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lizhen
 * @date 2020/7/20
 */
@Controller
@Slf4j
@RequestMapping("/dingtalk/isv/role")
public class DingtalkIsvRoleController {

    @Autowired
    private IDingtalkIsvRoleService dingtalkIsvRoleService;

    @RequestMapping("/list")
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = true) String corpId) throws Exception {
        OapiRoleListResponse oapiRoleListResponse = dingtalkIsvRoleService.listRole(corpId);
        return DingtalkResponseUtils.success("");
    }


}
