package com.fenbeitong.openapi.plugin.func.deprecated.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseResultEntity;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaThirdService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * module: 迁移openapi-java项目二期<br/>
 * <p>
 * description: openapi-java对外接口<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/11 17:36
 */
@RestController
@RequestMapping("/open/api")
public class OpenApiJavaThirdController {

    @Autowired
    private OpenApiJavaThirdService openApiJavaThirdService;


    @RequestMapping("/common/city-list")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询机场列表和火车列表")
    public OpenResponseResultEntity<?> cityList(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryCityList(httpRequest,request));
    }

    @RequestMapping("/third/departments/detail/by_ids")
    @FuncAuthAnnotation
    @ApiOperation(value = "根据部门ID集合查询部门详情")
    public OpenResponseResultEntity<?> thirdOrgUnitDetailByIds(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryOrgUnitDetailByIds(httpRequest,request));
    }

    @RequestMapping("/third/company/admin")
    @ApiOperation(value = "根据公司ID查询公司授权负责人")
    @FuncAuthAnnotation
    public OpenResponseResultEntity<?> getCompanyAdmin(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryAdmin(httpRequest,request));
    }

    @RequestMapping("/third/company/role/auth")
    @FuncAuthAnnotation
    @ApiOperation(value = "批量授权部门主管和角色")
    public OpenResponseResultEntity<?> getCompanyRoleAuth(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryCompanyRoleAuth(httpRequest,request));
    }

    @RequestMapping("/third/company/roles/get")
    @FuncAuthAnnotation
    @ApiOperation(value = "根据公司id 查询公司角色信息")
    public OpenResponseResultEntity<?> getCompanyRoles(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryCompanyRoles(httpRequest, request));
    }

    @RequestMapping("/approve/rule/info")
    @FuncAuthAnnotation
    @ApiOperation(value = "根据审批规则ID查询审批详情")
    public OpenResponseResultEntity<?> getApplyDetailById(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryApplyDetailById(httpRequest, request));
    }

    @RequestMapping("/approve/car/type")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询公司用车类型")
    public OpenResponseResultEntity<?> carApproveType(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openApiJavaThirdService.queryCarApproveType(httpRequest, request));
    }



}
