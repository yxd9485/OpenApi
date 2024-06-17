package com.fenbeitong.openapi.plugin.func.employee.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.employee.service.IEmployeeRankTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: FuncEmployeeTemplateController</p>
 * <p>Description: 职级权限模板</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/7/20 5:00 PM
 */
@RestController
@RequestMapping("/func/employee_template")
public class FuncEmployeeTemplateController {

    @Autowired
    private IEmployeeRankTemplateService employeeRankTemplateService;

    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest) {
        return FuncResponseUtils.success(employeeRankTemplateService.listTemplate((String) httpRequest.getAttribute("companyId")));
    }
}
