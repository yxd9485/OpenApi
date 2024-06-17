package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupFunctionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.OpenGroupFunctionService;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode.LACK_NECESSARY_PARAM;


/**
 * 功能簇子功能
 * Created by lizhen on 2020/01/10.
 */
@Controller
@RequestMapping("/definitions/function/openGroupFunction")
public class OpenGroupFunctionController {

    @Autowired
    public OpenGroupFunctionService openGroupFunctionService;

    /**
     * 添加功能簇子功能
     */
    @PostMapping
    @ResponseBody
    public Object createOpenGroupFunction(@Valid @RequestBody OpenGroupFunctionReqDTO req) {
        return DefinitionResultDTO.success(openGroupFunctionService.createOpenGroupFunction(req));
    }


    /**
     * 获取功能簇子功能
     */
    @PostMapping("/getOpenGroupFunction")
    @ResponseBody
    public Object getCompanyGroupFunction(@RequestBody OpenGroupFunctionReqDTO req) {
        String groupCode = req.getGroupCode();
        String functionCode = req.getFunctionCode();
        Map<String, Object> condition = new HashMap<>();
        condition.put("groupCode", groupCode);
        condition.put("functionCode", functionCode);
        return DefinitionResultDTO.success(openGroupFunctionService.listOpenGroupFunction(condition));
    }


}