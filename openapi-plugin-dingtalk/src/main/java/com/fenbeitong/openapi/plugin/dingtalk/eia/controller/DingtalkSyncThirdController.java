package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkSyncThirdEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingTalkEmployeeServiceExt1Impl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkJobServiceImpl;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: SyncThirdOrgToolController</p>
 * <p>Description: 同步第三方组织架构工具</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/11 5:40 PM
 */
@RestController
@RequestMapping("/dingtalk/syncThird")
@Api(value = "钉钉组织架构同步", tags = "钉钉组织架构同步", description = "钉钉组织架构同步")
public class DingtalkSyncThirdController {

    @Autowired
    private IDingTalkSyncThirdOrgService syncOrgService;

    @Autowired
    private IDingTalkSyncThirdEmployeeService syncEmployeeService;

    @Autowired
    private DingTalkEmployeeServiceExt1Impl syncEmployeeServiceExt1;

    @Autowired
    private DingtalkJobServiceImpl dingtalkJobService;

    /**
     * 按权限同步部门人员
     *
     * @param companyId
     * @param flag      0:true（增量）   1:false（全量）
     * @return
     */
    @RequestMapping("/syncThirdOrgByAuth/{companyId}/{flag}")
    public Object syncThirdOrgByAuth(@PathVariable("companyId") String companyId, @PathVariable("flag") String flag) {
        //同步部门人员
        syncEmployeeService.syncThirdOrgEmployeeByAuth(companyId, flag);
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/syncThirdOrg/{companyId}")
    @ApiOperation(value = "同步组织架构", notes = "同步组织架构", httpMethod = "POST", position = 1)
    public Object syncThirdOrg(@PathVariable("companyId") String companyId) {
        //同步部门
        //syncOrgService.syncThirdOrg(companyId);
        //同步人员
        //syncEmployeeService.syncThirdEmployee(companyId);
        //同步部门人员
        syncEmployeeService.syncThirdOrgEmployee(companyId);
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

//    @RequestMapping("/syncThirdDepartment/{companyId}")
//    @ApiOperation(value = "同步部门", notes = "同步部门", httpMethod = "POST", position = 2)
//    public Object syncThirdDepartment(@PathVariable("companyId") String companyId) {
//        //同步部门
//        syncOrgService.syncThirdOrg(companyId);
//        return DingtalkResponseUtils.success(Maps.newHashMap());
//    }
//
//    @RequestMapping("/syncThirdEmployee/{companyId}")
//    @ApiOperation(value = "同步人员", notes = "同步人员", httpMethod = "POST", position = 3)
//    public Object syncThirdEmployee(@PathVariable("companyId") String companyId) {
//        //同步人员
//        syncEmployeeService.syncThirdEmployee(companyId);
//        return DingtalkResponseUtils.success(Maps.newHashMap());
//    }

    @RequestMapping("/checkDingtalkDepartment/{companyId}")
    @ApiOperation(value = "检查部门", notes = "检查部门", httpMethod = "POST", position = 4)
    public Object checkDingtalkDepartment(@PathVariable("companyId") String companyId) {
        List<OapiDepartmentListResponse.Department> unSynDeptList = syncOrgService.checkDingtalkDepartment(companyId);
        if (!ObjectUtils.isEmpty(unSynDeptList)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_DEPARTMENT_UN_SYNC, String.join(",", unSynDeptList.stream().map(d -> d.getId() + ":" + d.getName()).collect(Collectors.toList())));
        }
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/checkDingtalkEmployee/{companyId}")
    @ApiOperation(value = "检查员工", notes = "检查员工", httpMethod = "POST", position = 5)
    public Object checkDingtalkEmployee(@PathVariable("companyId") String companyId) {
        List<DingtalkUser> unSynDingtalkUserList = syncEmployeeService.checkDingtalkEmployee(companyId);
        if (!ObjectUtils.isEmpty(unSynDingtalkUserList)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_UN_SYNC, String.join(",", unSynDingtalkUserList.stream().map(u -> u.getName() + ":" + u.getFbtMobile()).collect(Collectors.toList())));
        }
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/syncThirdOrg1/{companyId}")
    @ApiOperation(value = "同步组织架构(工号作为三方人员id)", notes = "同步组织架构(工号作为三方人员id)", httpMethod = "POST", position = 6)
    public Object syncDingtalkThirdOrg1(@PathVariable("companyId") String companyId) {
        //同步部门
        //syncOrgService.syncThirdOrg(companyId);
        //同步人员
        //syncEmployeeServiceExt1.syncThirdEmployee(companyId);
        //同步部门人员
        syncEmployeeServiceExt1.syncThirdOrgEmployee(companyId);
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

//    @RequestMapping("/syncThirdDepartment1/{companyId}")
//    @ApiOperation(value = "同步部门(工号作为三方人员id)", notes = "同步部门(工号作为三方人员id)", httpMethod = "POST", position = 7)
//    public Object syncThirdDepartment1(@PathVariable("companyId") String companyId) {
//        //同步部门
//        syncOrgService.syncThirdOrg(companyId);
//        return DingtalkResponseUtils.success(Maps.newHashMap());
//    }
//
//    @RequestMapping("/syncThirdEmployee1/{companyId}")
//    @ApiOperation(value = "同步人员(工号作为三方人员id)", notes = "同步人员(工号作为三方人员id)", httpMethod = "POST", position = 8)
//    public Object syncThirdEmployee1(@PathVariable("companyId") String companyId) {
//        //同步人员
//        syncEmployeeServiceExt1.syncThirdEmployee(companyId);
//        return DingtalkResponseUtils.success(Maps.newHashMap());
//    }

    @RequestMapping("/checkDingtalkDepartment1/{companyId}")
    @ApiOperation(value = "检查部门(工号作为三方人员id)", notes = "检查部门(工号作为三方人员id)", httpMethod = "POST", position = 9)
    public Object checkDingtalkDepartment1(@PathVariable("companyId") String companyId) {
        List<OapiDepartmentListResponse.Department> unSynDeptList = syncOrgService.checkDingtalkDepartment(companyId);
        if (!ObjectUtils.isEmpty(unSynDeptList)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_DEPARTMENT_UN_SYNC, String.join(",", unSynDeptList.stream().map(d -> d.getId() + ":" + d.getName()).collect(Collectors.toList())));
        }
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/checkDingtalkEmployee1/{companyId}")
    @ApiOperation(value = "检查员工(工号作为三方人员id)", notes = "检查员工(工号作为三方人员id)", httpMethod = "POST", position = 10)
    public Object checkDingtalkEmployee1(@PathVariable("companyId") String companyId) {
        List<DingtalkUser> unSynDingtalkUserList = syncEmployeeServiceExt1.checkDingtalkEmployee(companyId);
        if (!ObjectUtils.isEmpty(unSynDingtalkUserList)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_UN_SYNC, String.join(",", unSynDingtalkUserList.stream().map(u -> u.getName() + ":" + u.getFbtMobile()).collect(Collectors.toList())));
        }
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public void executeTask() {
        dingtalkJobService.start();
    }

    @RequestMapping("/syncDepartmentManagers/{companyId}")
    @ApiOperation(value = "部门主管同步，中间表对比", notes = "部门主管同步，中间表对比", httpMethod = "POST", position = 10)
    public Object syncDepartmentManagers(@PathVariable("companyId") String companyId) {
        syncEmployeeService.syncThirdOrgManagers(companyId);
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }
}
