package com.fenbeitong.openapi.plugin.func.deprecated.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseResultEntity;
import com.fenbeitong.openapi.plugin.func.deprecated.common.OpenResponseUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.employee.OpenEmployeeIDetailRespDTO;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenJavaService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;



/**
 * module: 迁移openapi-java项目<br/>
 * <p>
 * description: openapi-java对外接口<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/5 14:23
 * @since 2.0
 */
@RestController
@RequestMapping("/open/api")
public class OpenApiJavaController {

    @Autowired
    private OpenJavaService openJavaService;

    @Autowired
    private ValidService validService;

    @RequestMapping("/auth/v1/dispense")
    @ApiOperation(value = "API分发token")
    public OpenResponseResultEntity<?> auth(@RequestParam("app_id") String appId, @RequestParam("app_key") String appKey) {
        return OpenResponseUtils.success(openJavaService.getToken(appId, appKey));
    }

    @RequestMapping("/third/employees/info")
    @FuncAuthAnnotation
    @ApiOperation(value = "根据员工ID获取员工信息")
    public OpenResponseResultEntity<?> employeeInfo(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        OpenEmployeeIDetailRespDTO req = JsonUtils.toObj(request.getData(), OpenEmployeeIDetailRespDTO.class);
        validService.checkRequest(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setUserType(request.getEmployeeType());
        return OpenResponseUtils.success(openJavaService.getEmployeeInfo(req));
    }

    @RequestMapping("/third/project/create")
    @FuncAuthAnnotation
    @ApiOperation(value = "添加项目")
    public OpenResponseResultEntity<?> addThirdProject(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openJavaService.getAddThirdProject(httpRequest,request));
    }


    @RequestMapping("/third/project/update")
    @FuncAuthAnnotation
    @ApiOperation(value = "更新项目")
    public OpenResponseResultEntity<?> updateThirdProject(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openJavaService.getUpdateThirdProject(httpRequest,request));
    }

    @RequestMapping("/third/company/third/info")
    @FuncAuthAnnotation
    @ApiOperation(value = "获取公司人员信息")
    public OpenResponseResultEntity<?> companyEmployeeInfo(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openJavaService.getCompanyEmployeeInfo(httpRequest,request));
    }


    @RequestMapping("/third/departments/detail")
    @FuncAuthAnnotation
    @ApiOperation(value = "查询部门详情")
    public OpenResponseResultEntity<?> thirdOrgUnitDetail(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        return OpenResponseUtils.success(openJavaService.getThirdOrgUnitDetail(httpRequest,request));
    }

    @RequestMapping("/usercenter/queryThirdEmployee/v2")
    @ApiOperation(value = "查询三方人员信息")
    public OpenApiRespDTO<?> queryThirdEmployee(HttpServletRequest httpRequest) {
        return OpenResponseUtils.successV2(openJavaService.getQueryThirdEmployee(httpRequest));
    }
}
