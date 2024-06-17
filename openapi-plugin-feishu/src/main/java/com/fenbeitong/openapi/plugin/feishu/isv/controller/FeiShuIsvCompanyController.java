package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户授权免登
 *
 * @author lizhen
 * @date 2020/6/5
 */
@Controller
@Slf4j
@RequestMapping("/feishu/isv/tenant")
public class FeiShuIsvCompanyController {

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;


    @RequestMapping("/query")
    @ResponseBody
    public Object tenantQuery(HttpServletRequest request, HttpServletResponse response, String corpId,
                              Integer startIndex , Integer endIndex) throws Exception {
        Object companyNamelist = feiShuIsvCompanyAuthService.getCompanyNamelist(corpId , startIndex , endIndex );
        return companyNamelist;
    }


}
