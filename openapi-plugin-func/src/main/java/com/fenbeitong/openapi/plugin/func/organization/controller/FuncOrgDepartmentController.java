package com.fenbeitong.openapi.plugin.func.organization.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitManagersDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnitManagers;
import com.fenbeitong.openapi.plugin.support.organization.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.organization.dto.DepartmentReqDto;
import com.fenbeitong.openapi.plugin.support.organization.dto.OpenThirdOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.service.SupportFunDepartmentService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @Description 部门 同步
 * @Author duhui
 * @Date 2020-09-10
 **/
@Slf4j
@RestController
@RequestMapping("/func/department")
@Api(value = "FuncDepartmentController", description = "部门同步")
public class FuncOrgDepartmentController {
    @Autowired
    SupportFunDepartmentService funDepartmentService;
    @Autowired
    OpenThirdOrgUnitManagersDao openThirdOrgUnitManagersDao;


    @RequestMapping("/sync")
    @ApiOperation(value = "sync", notes = "部门同步", httpMethod = "POST", response = FuncResultEntity.class)
    public Object sync(@Valid DepartmentReqDto departmentReqDto) throws IOException {

        //signService.checkSign(departmentReqDto);
        CompletableFuture.supplyAsync(() -> funDepartmentService.DepartmentSync(departmentReqDto)).exceptionally(e -> {
            log.warn("{}", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/syncWithThirdTopId")
    @ApiOperation(value = "syncWithThirdTopId", notes = "按顶级部门id部门同步", httpMethod = "POST", response = FuncResultEntity.class)
    public Object syncWithThirdTopId(@Valid ApiRequestNoEmployee apiRequest) {
        funDepartmentService.syncWithThirdTopId(apiRequest);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @FuncAuthAnnotation
    @RequestMapping("/deleteBatch")
    @ApiOperation(value = "deleteBatch", notes = "删除部门", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteWithThirdTopId(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) {
        OpenThirdOrgUnitReqDTO openThirdOrgUnitReqDTO = JsonUtils.toObj(apiRequest.getData(), OpenThirdOrgUnitReqDTO.class);
        String companyId = (String) httpRequest.getAttribute("companyId");
        funDepartmentService.deleteBatch(openThirdOrgUnitReqDTO.getDepartmentList(), companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    @RequestMapping("/deleteOrgManagers")
    @ApiOperation(value = "deleteBatch", notes = "删除部门主管中间表", httpMethod = "POST", response = FuncResultEntity.class)
    public Object deleteOrgManager(String companyId, Integer status) {
        if (!ObjectUtils.isEmpty(companyId) && !ObjectUtils.isEmpty(status)) {
            Example example = new Example(OpenThirdOrgUnitManagers.class);
            example.createCriteria().andEqualTo("companyId", companyId).andEqualTo("status", status);
            openThirdOrgUnitManagersDao.deleteByExample(example);
        }
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
