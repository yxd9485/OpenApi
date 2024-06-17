package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenCompanyGroupFunctionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.OpenCompanyGroupFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


/**
 * 企业功能配置
 * Created by lizhen on 2020/01/10.
 */
@Controller
@RequestMapping("/definitions/function/openCompanyGroupFunction")
public class OpenCompanyGroupFunctionController {

    @Autowired
    public OpenCompanyGroupFunctionService openCompanyGroupFunctionService;

    /**
     * 添加企业功能配置
     */
    @PostMapping
    @ResponseBody
    public Object createOpenCompanyGroupFunction(@Valid @RequestBody OpenCompanyGroupFunctionReqDTO req) {
        return DefinitionResultDTO.success(openCompanyGroupFunctionService.createOpenCompanyGroupFunction(req));
    }


    /**
     * 启用
     */
    @PostMapping("/enable")
    @ResponseBody
    public Object enable(@Valid @RequestBody OpenCompanyGroupFunctionReqDTO req) {
        return DefinitionResultDTO.success(openCompanyGroupFunctionService.enable(req));
    }

    /**
     * 禁用
     */
    @PostMapping("/disable")
    @ResponseBody
    public Object disable(@Valid @RequestBody OpenCompanyGroupFunctionReqDTO req) {
        return DefinitionResultDTO.success(openCompanyGroupFunctionService.disable(req));
    }

    /**
     * 获取企业所有开通的功能
     */
    @GetMapping("{appId}")
    @ResponseBody
    public Object getCompanyGroupFunctionServiceByAppId(@PathVariable String appId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("appId", appId);
        return DefinitionResultDTO.success(openCompanyGroupFunctionService.getOpenCompanyGroupFunction(condition));
    }

}