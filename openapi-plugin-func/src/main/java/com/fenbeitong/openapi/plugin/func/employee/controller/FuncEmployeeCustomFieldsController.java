package com.fenbeitong.openapi.plugin.func.employee.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.employee.service.EmployeeCustomFieldsService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 自定义字段接口开放
 *
 * @author ctl
 * @date 2021/8/31
 */
@RestController
@RequestMapping("/func/employee/customField")
public class FuncEmployeeCustomFieldsController {

    @Autowired
    private EmployeeCustomFieldsService employeeCustomFieldsService;

    @FuncAuthAnnotation
    @PostMapping("/createOrUpdate")
    public Object createOrUpdate(@Validated ApiRequestBase apiRequest) {
        employeeCustomFieldsService.createOrUpdate(apiRequest);
        return FuncResponseUtils.success(new HashMap<>());
    }

    @FuncAuthAnnotation
    @PostMapping("/query")
    public Object query(HttpServletRequest request) {
        return FuncResponseUtils.success(employeeCustomFieldsService.query((String) request.getAttribute("companyId")));
    }
}
