package com.fenbeitong.openapi.plugin.func.employee.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.employee.dto.*;
import com.fenbeitong.openapi.plugin.support.employee.service.IEmployeeRankTemplateService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>Title: FuncEmployeeRankController</p>
 * <p>Description: 职级权限模板</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/7/20 5:00 PM
 */
@RestController
@RequestMapping("/func/employee_rank")
public class FuncEmployeeRankController {

    @Autowired
    private IEmployeeRankTemplateService employeeRankTemplateService;

    @Autowired
    private CommonAuthService signService;

    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object uploadBatch(HttpServletRequest httpRequest) {
        return FuncResponseUtils.success(employeeRankTemplateService.listRank((String) httpRequest.getAttribute("companyId")));
    }

    @FuncAuthAnnotation
    @RequestMapping("/add")
    public Object addOrUpdateAuthRank(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        AddAuthRankReqDTO req = JsonUtils.toObj(apiRequest.getData(), AddAuthRankReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        Object rankId = employeeRankTemplateService.addOrUpdateAuthRank(req, appId);
        return FuncResponseUtils.success(rankId);
    }

    @FuncAuthAnnotation
    @RequestMapping("/add_employee")
    public Object addRankEmployee(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        AddRankEmployeeReqDTO req = JsonUtils.toObj(apiRequest.getData(), AddRankEmployeeReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        employeeRankTemplateService.addRankEmployee(appId,req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/employee_list")
    public Object getRankEmployeeList(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        AuthRankEmployeeListReqDTO req = JsonUtils.toObj(apiRequest.getData(), AuthRankEmployeeListReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        List<AuthRankEmployeeListResDTO> authRankEmployeeList = employeeRankTemplateService.getAuthRankEmployeeList(appId, req);
        return FuncResponseUtils.success(authRankEmployeeList);
    }

    @FuncAuthAnnotation
    @RequestMapping("/bind")
    public Object bindAuthRank(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        BindAuthRankReqDTO req = JsonUtils.toObj(apiRequest.getData(), BindAuthRankReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        employeeRankTemplateService.bindAuthRank(appId, req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/delete")
    public Object deleteAuthRank(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        DeleteAuthRankReqDTO req = JsonUtils.toObj(apiRequest.getData(), DeleteAuthRankReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        employeeRankTemplateService.deleteAuthRank(appId, req.getThirdRankId());
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/add_employee_batch")
    public Object addEmployeeBatch(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        AuthRankEmployeeBatchReqDTO req = JsonUtils.toObj(apiRequest.getData(), AuthRankEmployeeBatchReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        employeeRankTemplateService.addEmployeeBatch(appId, req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/delete_employee_batch")
    public Object deleteEmployeeBatch(@Valid ApiRequestNoEmployee apiRequest) throws BindException {
        String appId = signService.getAppId(apiRequest);
        AuthRankEmployeeBatchReqDTO req = JsonUtils.toObj(apiRequest.getData(), AuthRankEmployeeBatchReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        employeeRankTemplateService.deleteEmployeeBatch(appId, req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
