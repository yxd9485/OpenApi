package com.fenbeitong.openapi.plugin.func.employee.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.employee.dto.EmployeeReqDto;
import com.fenbeitong.openapi.plugin.support.employee.dto.OpenThirdEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.SupportEmployeeService;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Description 人员同步
 * @Author duhui
 * @Date 2020-09-10
 **/
@Slf4j
@RestController
@RequestMapping("/func/employee")
@Api(value = "FuncEmployeeController", description = "人员同步")
public class FuncOrgEmployeeController {
    @Autowired
    SupportEmployeeService employeeService;


    /**
     * 暂时不用
     */
    @RequestMapping("/sync")
    @ApiOperation(value = "sync", notes = "人员同步", httpMethod = "POST", response = FuncResultEntity.class)
    public Object sync(@Valid EmployeeReqDto employeeReqDto) throws IOException {
        CompletableFuture.supplyAsync(() -> employeeService.EmployeeSync(employeeReqDto)).exceptionally(e -> {
            log.warn("{}", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/syncWithThirdTopId")
    @ApiOperation(value = "syncWithThirdTopId", notes = "按顶级部门id同步人员", httpMethod = "POST", response = FuncResultEntity.class)
    public Object syncWithThirdTopId(@Valid ApiRequestNoEmployee apiRequest) {
        employeeService.syncWithThirdTopId(apiRequest);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/deleteBatch")
    @ApiOperation(value = "deleteBatch", notes = "批昰删除人员", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteBatch(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) {
        String companyId = (String) httpRequest.getAttribute("companyId");
        OpenThirdEmployeeReqDTO openThirdEmployeeReqDTO = JsonUtils.toObj(apiRequest.getData(), OpenThirdEmployeeReqDTO.class);
        List<OpenThirdEmployeeDTO> employeeList = openThirdEmployeeReqDTO.getEmployeeList();
        employeeService.deleteBatch(employeeList, companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    /**
     * 更新人员权限  读取open_authority_set 表的权限 更新uc人员数据
     */

    @FuncAuthAnnotation
    @RequestMapping("/updateDefaultAuth")
    @ApiOperation(value = "updateDefaultAuth", notes = "更新人员权限", httpMethod = "POST", response = FuncResultEntity.class)
    public Object updateDefaultAuth(@Valid ApiRequestNoEmployee apiRequest) {
        Map<String, String> map = JsonUtils.toObj(apiRequest.getData(), Map.class);
        return OpenapiResponseUtils.success(employeeService.updateEmployeeAuth(map));
    }

}
