package com.fenbeitong.openapi.plugin.func.deprecated.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseResultEntity;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaBudgetService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * module: 迁移 open-java 项目<br/>
 * <p>
 * description: 预算模块 <br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/11 19:50
 */
@RestController
@RequestMapping("/open/api/third/budget")
public class OpenApiJavaBudgetController {

    @Autowired
    private OpenApiJavaBudgetService openApiJavaBudgetService;

    @RequestMapping("/create")
    @FuncAuthAnnotation
    @ApiOperation(value = "新增第三方预算")
    public OpenResponseResultEntity<?> createThirdBudget(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.createBudget(httpRequest, request));
    }

    @RequestMapping("/update")
    @FuncAuthAnnotation
    @ApiOperation(value = "更新三方预算")
    public OpenResponseResultEntity<?> updateThirdBudget(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.updateBudget(httpRequest, request));
    }

    @RequestMapping("/delete")
    @FuncAuthAnnotation
    @ApiOperation(value = "删除预算")
    public OpenResponseResultEntity<?> deleteThirdBudget(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.deleteBudget(httpRequest, request));
    }

    @RequestMapping("/list")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询第三方运算列表")
    public OpenResponseResultEntity<?> budgetList(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.queryBudgetList(httpRequest, request));
    }

    @RequestMapping("/apply/save")
    @FuncAuthAnnotation
    @ApiOperation(value = "第三方预算应用保存")
    public OpenResponseResultEntity<?> thirdBudgetApplySave(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.saveApplyBudget(httpRequest, request));
    }

    @RequestMapping("/detail")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询第三方预算详情")
    public OpenResponseResultEntity<?> getThirdBudgetDetail(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.queryBudgetDetail(httpRequest, request));
    }


    @RequestMapping("/progress")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询第三方预算进度")
    public OpenResponseResultEntity<?> budgetProgress(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaBudgetService.queryBudgetProgress(httpRequest, request));
    }


}

