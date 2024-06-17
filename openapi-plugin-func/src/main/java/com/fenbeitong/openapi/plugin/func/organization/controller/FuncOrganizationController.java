package com.fenbeitong.openapi.plugin.func.organization.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.organization.service.FuncOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.organization.dto.OpenThirdOrgUnitBatchReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.OpenThirdOrgUnitListReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.service.SupportFunDepartmentService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 功能集成-组织架构控制器
 * Created by log.chang on 2019/12/3.
 */
@RestController
@RequestMapping("/func/department")
@Api(value = "部门管理", tags = "部门管理", description = "部门管理")
public class FuncOrganizationController {

    @Autowired
    private FuncOrganizationService funcOrganizationService;
    @Autowired
    SupportFunDepartmentService funDepartmentService;

    @RequestMapping("/create")
    @ApiOperation(value = "添加部门", notes = "添加部门", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createDepartment(@Valid ApiRequest apiRequest) throws Exception {
        return funcOrganizationService.createDepartment(apiRequest);
    }

    @RequestMapping("/update")
    @ApiOperation(value = "更新部门", notes = "更新部门", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateDepartment(@Valid ApiRequest apiRequest) throws Exception {
        return funcOrganizationService.updateDepartment(apiRequest);
    }

    @RequestMapping("/delete")
    @ApiOperation(value = "删除部门", notes = "删除部门", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteDepartment(@Valid ApiRequest apiRequest) throws Exception {
        return funcOrganizationService.deleteDepartment(apiRequest);
    }

    @RequestMapping("/bind")
    @ApiOperation(value = "绑定部门", notes = "绑定部门", httpMethod = "POST", response = FuncResultEntity.class)
    public Object bindDepartment(@Valid ApiRequest apiRequest) throws Exception {
        return funcOrganizationService.bindDepartment(apiRequest);
    }

    @FuncAuthAnnotation
    @RequestMapping("/uploadBatch")
    @ApiOperation(value = "uploadBatch", notes = "批量上传部门信息", httpMethod = "POST", response = FuncResultEntity.class)
    public Object uploadBatch(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws BindException {
        OpenThirdOrgUnitBatchReqDTO req = JsonUtils.toObj(apiRequest.getData(), OpenThirdOrgUnitBatchReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        funDepartmentService.uploadBatch(req);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/list")
    @ApiOperation(value = "departmentList", notes = "查询部门列表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object departmentList(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws BindException {

        OpenThirdOrgUnitListReqDTO req = JsonUtils.toObj(apiRequest.getData(), OpenThirdOrgUnitListReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return OpenapiResponseUtils.success(funDepartmentService.queryByPage(req));
    }
}
