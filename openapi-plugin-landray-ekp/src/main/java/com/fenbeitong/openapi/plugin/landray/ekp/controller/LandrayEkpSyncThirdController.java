package com.fenbeitong.openapi.plugin.landray.ekp.controller;

import com.fenbeitong.openapi.plugin.landray.ekp.common.LandaryEkpResponseUtils;
import com.fenbeitong.openapi.plugin.landray.ekp.service.ILandaryEkpSyncOrgEmployeeService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lizhen
 * @date 2021/1/27
 */
@RestController
@RequestMapping("/landary/ekp/syncThird")
public class LandrayEkpSyncThirdController {

    @Autowired
    private ILandaryEkpSyncOrgEmployeeService landaryEkpSyncOrgEmployeeService;

    @RequestMapping("/syncThirdOrgEmployee")
    public Object syncOrgEmployee(@RequestParam(value = "companyId", required = true) String companyId) {
        landaryEkpSyncOrgEmployeeService.syncThirdOrgEmployee(companyId);
        return LandaryEkpResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/syncThirdOrgEmployeeV2")
    public Object syncOrgEmployeeV2(@RequestParam(value = "companyId",required = true) String companyId, @RequestParam(value = "rootId",required = false) String rootId) {
        landaryEkpSyncOrgEmployeeService.syncThirdOrgEmployeeV2(companyId, rootId);
        return LandaryEkpResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/queryLandaryEkpDepartmentInfo")
    public Object queryLandaryEkpDepartmentInfo(@RequestParam(value = "companyId",required = true) String companyId) {
        return LandaryEkpResponseUtils.success(landaryEkpSyncOrgEmployeeService.queryLandaryEkpDepartmentInfo(companyId));
    }

}
