package com.fenbeitong.openapi.plugin.customize.wawj.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjAuthRespDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户授权免登
 *
 * @author lizhen
 * @date 2020/10/24
 */
@RestController
@Slf4j
@RequestMapping("/customize/wawj/auth")
public class WawjUserAuthController {

    @Autowired
    IWawjAuthService wawjAuthService;

    @RequestMapping()
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String code, @RequestParam String companyId) throws Exception {
        WawjAuthRespDTO authInfo = wawjAuthService.auth(code, companyId);
        return OpenapiResponseUtils.success(authInfo);
    }
}
