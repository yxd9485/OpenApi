package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.permission.CreatePermissionDefinitionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.PermissionDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 对接三方权限定义
 * Created by log.chang on 2019/12/14.
 */
@Controller
@RequestMapping("/definitions/plugin/permission")
public class PermissionDefinitionController {

    @Autowired
    private PermissionDefinitionService permissionDefinitionService;

    /**
     * 添加企业权限
     */
    @PostMapping
    @ResponseBody
    public Object createPermission(@RequestBody CreatePermissionDefinitionReqDTO req) {
        return DefinitionResultDTO.success(permissionDefinitionService.createPermission(req));
    }

    /**
     * 查询企业权限
     */
    @GetMapping("{appId}")
    @ResponseBody
    public Object listPermission(@PathVariable String appId) {
        return DefinitionResultDTO.success(permissionDefinitionService.listPermission(appId));
    }

    /**
     * 删除权限配置
     */
    @DeleteMapping("{permissionId}")
    @ResponseBody
    public Object deletePermission(@PathVariable Integer permissionId) {
        return DefinitionResultDTO.success(permissionDefinitionService.deletePermission(permissionId));
    }

}
