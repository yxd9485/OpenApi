package com.fenbeitong.openapi.plugin.func.company.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.company.service.impl.ICompanyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>Title: FuncCompanyController</p>
 * <p>Description: 公司信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/22 8:12 PM
 */
@RestController
@RequestMapping("/func/company")
public class FuncCompanyController {

    @Autowired
    private ICompanyServiceImpl companyService;

    @RequestMapping("/isDocking/{companyId}")
    public Object isDocking(@PathVariable("companyId") String companyId) {
        Map<String, Object> result = companyService.isDocking(companyId);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/isApproveDocking/{companyId}")
    public Object isApproveDocking(@PathVariable("companyId") String companyId) {
        Map<String, Object> result = companyService.isApproveDocking(companyId);
        return FuncResponseUtils.success(result);
    }


    @RequestMapping("/getRules/{companyId}")
    public Object getRules(@PathVariable("companyId") String companyId) {
        Map companyRules = companyService.getCompanyRules(companyId);
        return FuncResponseUtils.success(companyRules);
    }




}
