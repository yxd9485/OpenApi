package com.fenbeitong.openapi.plugin.func.organization.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.employee.dto.OrgDto;
import com.fenbeitong.openapi.plugin.func.organization.service.Func51TalkEmployeeAndDepartmentService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * @Description 部门同步
 * @Author duhui
 * @Date 2020-09-10
 **/
@RestController
@RequestMapping("/func/employeeAndDepartment")
@Api(value = "FuncOrgEmployeeDepartmentController", description = "组织架构同步")
@Slf4j
public class Func51TalkOrgEmployeeDepartmentController {
    @Autowired
    Func51TalkEmployeeAndDepartmentService funcEmployeeAndDepartmentService;

    @FuncAuthAnnotation
    @RequestMapping("/sync")
    @ApiOperation(value = "sync", notes = "部门同步", httpMethod = "POST", response = FuncResultEntity.class)
    public Object sync(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequestBase) {
        log.info("FuncOrgEmployeeDepartmentController  请求数据{}", JsonUtils.toJson(apiRequestBase));
        OrgDto orgDto = JsonUtils.toObj(apiRequestBase.getData(), OrgDto.class);
        String companyId = ((String) httpRequest.getAttribute("companyId"));
        CompletableFuture.supplyAsync(() -> funcEmployeeAndDepartmentService.EmployeeAndDepartmentSync(orgDto, companyId)).exceptionally(e -> {
            log.warn("同步异常", e);
            return null;
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    @RequestMapping("/updateMasterIds/{companyId}")
    @ApiOperation(value = "updateMasterIds", notes = "部门配置同步", httpMethod = "POST")
    public Object updateMasterIds(@PathVariable("companyId") String companyId) {
        CompletableFuture.supplyAsync(() -> funcEmployeeAndDepartmentService.updateMasterIds(companyId)).exceptionally(e -> {
            log.warn("设置部门负责人同步异常", e);
            return null;
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


}
