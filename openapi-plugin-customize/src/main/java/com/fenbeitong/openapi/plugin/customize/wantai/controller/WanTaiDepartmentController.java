package com.fenbeitong.openapi.plugin.customize.wantai.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ArchiveCallbackReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.DepartmentRequestDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiDepartmentServicce;
import com.fenbeitong.openapi.plugin.func.annotation.SecurityAnnotation;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 部门同步
 * @author lizhen
 */
@RestController
@RequestMapping("/gate/customize/wantai/department")
public class WanTaiDepartmentController {

    @Autowired
    private WanTaiDepartmentServicce wanTaiDepartmentServicce;

    @SecurityAnnotation
    @RequestMapping("/sync")
    public Object push(HttpServletRequest request, @RequestBody @Valid DepartmentRequestDTO departmentRequest) {
        departmentRequest.setCompanyId(request.getAttribute("companyId").toString());
        return wanTaiDepartmentServicce.sync(departmentRequest);
    }

}
