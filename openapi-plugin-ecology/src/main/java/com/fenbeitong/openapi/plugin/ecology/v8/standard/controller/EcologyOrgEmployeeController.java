package com.fenbeitong.openapi.plugin.ecology.v8.standard.controller;

import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyResponseUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyRestSyncThirdOrgEmployeeService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologySyncThirdOrgEmployeeService;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lizhen on 2020/12/9.
 */
@RestController
@RequestMapping("/ecology/standard/")
public class EcologyOrgEmployeeController {

    @Autowired
    private IEcologySyncThirdOrgEmployeeService ecologySyncThirdOrgEmployeeService;

    @Autowired
    private IEcologyRestSyncThirdOrgEmployeeService ecologyRestSyncThirdOrgEmployeeService;

    @RequestMapping("/syncThirdOrgEmployee/{companyId}")
    public Object syncThirdOrgEmployee(@PathVariable("companyId") String companyId) {
        ecologySyncThirdOrgEmployeeService.syncThirdOrgEmployee(companyId);
        return EcologyResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/syncThirdOrgEmployeePage/{companyId}")
    public Object syncThirdOrgEmployeePage(@PathVariable("companyId") String companyId) {
        ecologySyncThirdOrgEmployeeService.syncThirdOrgEmployeePage(companyId);
        return EcologyResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/e9/syncThirdOrgEmployee/{companyId}")
    public Object syncRestThirdOrgEmployee(@PathVariable("companyId") String companyId) {
        ecologyRestSyncThirdOrgEmployeeService.restSyncThirdOrgEmployee(companyId);
        return EcologyResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/e9/syncDepartmentManagers/{companyId}")
    public Object syncDepartmentManagers(@PathVariable("companyId") String companyId){
        ecologyRestSyncThirdOrgEmployeeService.syncDepartmentManagers(companyId);
        return EcologyResponseUtils.success(Maps.newHashMap());
    }
}
