package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.company.auth.AuthRegisterReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.AuthDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 企业对接授权配置
 * Created by log.chang on 2019/12/13.
 */
@Controller
@RequestMapping("/definitions/company/auth")
public class AuthDefinitionController {

    @Autowired
    private AuthDefinitionService authDefinitionService;

    /**
     * 注册企业对接
     */
    @PostMapping
    @ResponseBody
    public Object register(@RequestBody AuthRegisterReqDTO req) {
        return DefinitionResultDTO.success(authDefinitionService.register(req));
    }

    /**
     * 上架
     */
    @PatchMapping("{appId}/enable")
    @ResponseBody
    public Object enable(@PathVariable String appId) {
        return DefinitionResultDTO.success(authDefinitionService.enable(appId));
    }

    /**
     * 下架
     */
    @PatchMapping("{appId}/disable")
    @ResponseBody
    public Object disable(@PathVariable String appId) {
        return DefinitionResultDTO.success(authDefinitionService.disable(appId));
    }

    /**
     * 下架
     */
    @GetMapping("{appId}")
    @ResponseBody
    public Object getAuthInfo(@PathVariable String appId) {
        return DefinitionResultDTO.success(authDefinitionService.getAuthInfo(appId));
    }

}
