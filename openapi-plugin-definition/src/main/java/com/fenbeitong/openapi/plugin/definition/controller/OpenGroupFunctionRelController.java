package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionRelReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.OpenGroupFunctionRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


/**
 * 功能簇子功能前置条件
 * Created by lizhen on 2020/01/10.
 */
@Controller
@RequestMapping("/definitions/function/openGroupFunctionRel")
public class OpenGroupFunctionRelController {

    @Autowired
    public OpenGroupFunctionRelService openGroupFunctionRelService;

    /**
     * 添加功能簇子功能前置条件
     */
    @PostMapping
    @ResponseBody
    public Object createOpenGroupFunctionRel(@Valid @RequestBody OpenGroupFunctionRelReqDTO req) {
        return DefinitionResultDTO.success(openGroupFunctionRelService.createOpenGroupFunctionRel(req));
    }

    @GetMapping("{groupFunctionCode}")
    @ResponseBody
    public Object getOpenGroupFunctionRel(@PathVariable String groupFunctionCode) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("groupFunctionCode", groupFunctionCode);
        return DefinitionResultDTO.success(openGroupFunctionRelService.listOpenGroupFunctionRel(condition));
    }

    /**
     * 删除功能簇子功能前置条件
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public Object deleteOpenGroupFunctionRel(@Valid @RequestBody OpenGroupFunctionRelReqDTO req) {
        return DefinitionResultDTO.success(openGroupFunctionRelService.deleteOpenGroupFunctionRel(req));
    }

}