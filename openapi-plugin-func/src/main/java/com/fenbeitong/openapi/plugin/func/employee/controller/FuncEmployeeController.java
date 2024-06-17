package com.fenbeitong.openapi.plugin.func.employee.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.dto.OpenThirdEmployeeBatchReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.SupportEmployeeService;
import com.fenbeitong.openapi.plugin.support.organization.dto.ApiRequestNoEmployee;
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
 * 功能集成-用户控制器
 * Created by log.chang on 2019/12/3.
 */
@RestController
@RequestMapping("/func/employee")
@Api(value = "员工管理", tags = "员工管理", description = "员工管理")
public class FuncEmployeeController {

    @Autowired
    private FuncEmployeeService funcEmployeeService;

    @Autowired
    SupportEmployeeService employeeService;

    @RequestMapping("/create")
    @ApiOperation(value = "添加员工", notes = "添加员工", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createUser(@Valid ApiRequest apiRequest) throws Exception {
        return funcEmployeeService.createUser(apiRequest);
    }

    @RequestMapping("/update")
    @ApiOperation(value = "更新员工", notes = "更新员工", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateUser(@Valid ApiRequest apiRequest) throws Exception {
        return funcEmployeeService.updateUser(apiRequest);
    }

    @RequestMapping("/delete")
    @ApiOperation(value = "删除员工", notes = "删除员工", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteUser(@Valid ApiRequest apiRequest) throws Exception {
        return funcEmployeeService.deleteUser(apiRequest);
    }

    @RequestMapping("/bind")
    @ApiOperation(value = "绑定员工", notes = "绑定员工", httpMethod = "POST", response = FuncResultEntity.class)
    public Object bindUser(@Valid ApiRequest apiRequest) throws Exception {
        return funcEmployeeService.bindUser(apiRequest);
    }

    @FuncAuthAnnotation
    @RequestMapping("/uploadBatch")
    @ApiOperation(value = "uploadBatch", notes = "批量上传人员信息", httpMethod = "POST", response = FuncResultEntity.class)
    public Object uploadBatch(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws BindException {
        OpenThirdEmployeeBatchReqDTO req = JsonUtils.toObj(apiRequest.getData(), OpenThirdEmployeeBatchReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        employeeService.uploadBatch(req);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

//    @FuncAuthAnnotation
//    @RequestMapping("/doBatch")
//    @ApiOperation(value = "doBatch", notes = "组织机构人员批量执行", httpMethod = "POST", response = FuncResultEntity.class)
//    public Object doBatch(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws BindException {
//        OpenThirdEmployeeDoBatchReqDTO req = JsonUtils.toObj(apiRequest.getData(), OpenThirdEmployeeDoBatchReqDTO.class);
//        ValidatorUtils.validateBySpring(req);
//        String companyId = (String) httpRequest.getAttribute("companyId");
//        employeeService.doBatch(companyId);
//        return OpenapiResponseUtils.success(Maps.newHashMap());
//    }

}
