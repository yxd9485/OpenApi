package com.fenbeitong.openapi.plugin.func.employee.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenAuthoritySetDTO;
import com.fenbeitong.openapi.plugin.support.common.entity.OpenAuthoritySet;
import com.fenbeitong.openapi.plugin.support.common.service.OpenAuthoritySetService;
import com.fenbeitong.openapi.plugin.support.employee.service.SupportEmployeeService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>Title: FuncEmployeeInitController</p>
 * <p>Description: 初始化人员控制器</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/19 7:51 PM
 */
@RestController
@RequestMapping("/func/employee/init")
public class FuncEmployeeInitController {

    @Autowired
    private OpenEmployeeExtServiceImpl openEmployeeExtService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private OpenAuthoritySetService openAuthoritySetService;

    @Autowired
    private SupportEmployeeService supportEmployeeService;

    @RequestMapping("/doDelete/{cacheId}")
    public Object doDelete(@PathVariable("cacheId") String cacheId) {
        openEmployeeExtService.doDeleteEmployee(cacheId);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/deleteOpenThirdEmployeeAndOrgUnit")
    public Object deleteOpenThirdEmployeeAndOrgUnit(@RequestParam(value = "openType", required = true) Integer openType, @RequestParam(value = "companyId", required = true) String companyId) {
        openSyncThirdOrgService.deleteOpenEmployeeAndOrgUnit(openType, companyId);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/createOpenAuthoritySet")
    public Object createOpenAuthoritySet(@Valid @RequestBody OpenAuthoritySetDTO openAuthoritySetDTO) throws BindException {
        openAuthoritySetService.createOpenAuthoritySet(openAuthoritySetDTO);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/updateOpenAuthoritySet")
    public Object updateOpenAuthoritySet(@Valid @RequestBody OpenAuthoritySetDTO openAuthoritySetDTO) throws BindException {
        openAuthoritySetService.updateOpenAuthoritySet(openAuthoritySetDTO);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/deleteOpenAuthoritySet")
    public Object deleteOpenAuthoritySet(@RequestParam(value = "companyId", required = true) String companyId) throws BindException {
        openAuthoritySetService.deleteOpenAuthoritySet(companyId);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/getOpenAuthoritySet")
    public Object getOpenAuthoritySet(@RequestParam(value = "companyId", required = true) String companyId) throws BindException {
        List<OpenAuthoritySet> openAuthoritySet = openAuthoritySetService.getOpenAuthoritySetList(companyId);
        return FuncResponseUtils.success(openAuthoritySet);
    }

    @RequestMapping("/doBatch")
    @Async
    public Object doBatch(@RequestParam(value = "companyId", required = false) String companyId, @RequestParam(value = "timeRange", required = true) Integer timeRange) {
        supportEmployeeService.doBatch(companyId, timeRange);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/getDeleteEmployee/{cacheId}")
    public Object getDeleteEmployee(@PathVariable("cacheId") String cacheId) {
        return FuncResponseUtils.success(openEmployeeExtService.getDeleteEmployee(cacheId));
    }
}
