package com.fenbeitong.openapi.plugin.func.sso.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.sso.service.IYuFuSsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: FbtWebSsoController</p>
 * <p>Description: 分贝通企业web单点登录</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 7:33 PM
 */
@RestController
@RequestMapping("/func/yufu/sso")
public class YufuSsoController {

    @Autowired
    private IYuFuSsoService yuFuSsoService;

    @RequestMapping("/loginWeb/{companyId}")
    public Object loginWeb(@PathVariable("companyId") String companyId, @RequestParam("id_token") String token) {
        return yuFuSsoService.loginWeb(companyId, token);
    }

    @RequestMapping("/loginWebapp/{companyId}")
    public Object loginWebapp(@PathVariable("companyId") String companyId, @RequestParam("id_token") String token) {
        Object result = yuFuSsoService.loginWebapp(companyId, token);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/getWebLoginInfo/{id}")
    public Object getWebLoginInfo(@PathVariable("id") String id) {
        Object webLoginInfo = yuFuSsoService.getWebLoginInfo(id);
        return FuncResponseUtils.success(webLoginInfo);
    }

}
