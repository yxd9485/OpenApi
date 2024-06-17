package com.fenbeitong.openapi.plugin.func.deprecated.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseResultEntity;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaProjectService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * module: 迁移 open-java 项目<br/>
 * <p>
 * description: 项目模块<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/16 17:46
 */
@RestController
@RequestMapping("/open/api/third/project")
public class OpenApiJavaProjectController {

    @Autowired
    private OpenApiJavaProjectService openApiJavaProjectService;

    @FuncAuthAnnotation
    @RequestMapping("/updateState")
    @ApiOperation(value = "项目状态更新")
    public OpenResponseResultEntity<?> changeUpdateState(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaProjectService.updateState(httpRequest, request));
    }

    @FuncAuthAnnotation
    @RequestMapping("/updateStateBatch")
    @ApiOperation(value = "项目状态更新")
    public OpenResponseResultEntity<?> changeUpdateStateBatch(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        Object o = openApiJavaProjectService.updateStateBatch(httpRequest, request);
        String result = JsonUtils.toJson(o);
        Map<String, Object> map = JsonUtils.toObj(result, new TypeReference<Map<String, Object>>() {
        });
        Integer code = (Integer) map.get("code");
        if (code == 0) {
            return OpenResponseUtils.success(map.get("data"));
        }
        return OpenResponseUtils.fail(result);
    }

    @FuncAuthAnnotation
    @RequestMapping("/list")
    @ApiOperation(value = "项目状态更新")
    public OpenResponseResultEntity<?> list(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaProjectService.projectList(httpRequest, request));
    }

    @FuncAuthAnnotation
    @RequestMapping("/createBatch")
    @ApiOperation(value = "批量创建项目")
    public OpenResponseResultEntity<?> projectCreateBatch(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaProjectService.createBatch(httpRequest, request));
    }
}
