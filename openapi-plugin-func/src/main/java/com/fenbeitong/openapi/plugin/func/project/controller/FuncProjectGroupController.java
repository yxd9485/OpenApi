package com.fenbeitong.openapi.plugin.func.project.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.project.service.FuncGroupService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/10/22 下午2:19
 */

@RestController
@RequestMapping("/func/third/group")
@Api(value = "项目分组", tags = "项目分组", description = "项目分组")
public class FuncProjectGroupController {

    @Autowired
    private FuncGroupService funcGroupService;

    @RequestMapping("/create")
    @ApiOperation(value = "添加第三方项目分组", notes = "添加第三方项目分组", httpMethod = "POST", response = FuncResultEntity.class)
    public Object addThirdProject(@Valid ApiRequestBase apiRequest) throws Exception {
        Object result = funcGroupService.addGroup(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/update")
    @ApiOperation(value = "修改第三方项目分组", notes = "修改第三方项目分组", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateThirdProject(@Valid ApiRequestBase apiRequest) throws Exception {
        Object result = funcGroupService.updateGroup(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/delete")
    @ApiOperation(value = "删除第三方项目分组", notes = "删除第三方项目分组", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteThirdProject(@Valid ApiRequestBase apiRequest) throws Exception {
        funcGroupService.deleteGroup(apiRequest);
        return FuncResponseUtils.success(null);
    }

    @RequestMapping("/list")
    @ApiOperation(value = "查询第三方项目分组列表", notes = "查询第三方项目分组列表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object thirdProjectList(@Valid ApiRequestBase apiRequest) throws Exception {
        Object result = funcGroupService.list(apiRequest);
        return FuncResponseUtils.success(result);
    }

}
