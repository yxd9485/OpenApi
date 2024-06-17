package com.fenbeitong.openapi.plugin.func.project.controller;

/**
 * 功能集成-项目控制器
 * Created by log.chang on 2019/12/3.
 */

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.project.service.FuncProjectService;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 项目中心
 */
@RestController
@RequestMapping("/func/third/project")
@Api(value = "项目中心", tags = "项目中心", description = "项目中心")
public class FuncProjectController {

    @Autowired
    private FuncProjectService funcProjectService;

    @RequestMapping("/create")
    @ApiOperation(value = "添加第三方项目", notes = "添加第三方项目", httpMethod = "POST", response = FuncResultEntity.class)
    public Object addThirdProject(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.addThirdProject(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/update")
    @ApiOperation(value = "更新第三方项目", notes = "更新第三方项目", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateThirdProject(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.updateThirdProject(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/updateState")
    @ApiOperation(value = "更新第三方项目状态", notes = "更新第三方项目状态", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateThirdProjectState(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.updateThirdProjectState(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/updateStateBatch")
    @ApiOperation(value = "批量更新第三方项目状态", notes = "批量更新第三方项目状态", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateThirdProjectStateByBatch(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.updateThirdProjectStateByBatch(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/createBatch")
    @ApiOperation(value = "批量新增第三方项目", notes = "批量新增第三方项目", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createThirdProjectByBatch(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.createThirdProjectByBatch(apiRequest);
        return FuncResponseUtils.success(result);
    }


    @RequestMapping("/list")
    @ApiOperation(value = "第三方项目列表", notes = "第三方项目列表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object listThirdProject(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.listThirdProject(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/get")
    @ApiOperation(value = "第三方项目详情", notes = "第三方项目详情", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getThirdProject(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.getThirdProject(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/listApp")
    @ApiOperation(value = "第三方项目列表app端", notes = "第三方项目列表app端", httpMethod = "POST", response = FuncResultEntity.class)
    public Object listThirdProjectApp(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcProjectService.listThirdProjectApp(apiRequest);
        return FuncResponseUtils.success(result);
    }



}
