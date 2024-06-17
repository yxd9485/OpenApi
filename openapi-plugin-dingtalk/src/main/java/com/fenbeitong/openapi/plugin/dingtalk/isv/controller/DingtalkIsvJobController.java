package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.job.DingtalkIsvJobService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.job.OpenSyncBizDataJobService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.job.OpenSyncBizDataMediumjJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lizhen
 * @date 2020/6/12
 */
@Controller
@Slf4j
@RequestMapping("/dingtalk/isv/job")
public class DingtalkIsvJobController {

    @Autowired
    private DingtalkIsvJobService dingtalkIsvJobService;

    @Autowired
    private OpenSyncBizDataJobService openSyncBizDataJobService;

    @Autowired
    private OpenSyncBizDataMediumjJobService openSyncBizDataMediumjJobService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public String executeTask() {
        dingtalkIsvJobService.start();
        return "ok";
    }

    @RequestMapping("/dingtalkcloud/execute")
    @Async
    @ResponseBody
    public String openSyncBizDataTask() {
        openSyncBizDataJobService.start();
        return "ok";
    }

    @RequestMapping("/dingtalkcloud/medium/execute")
    @Async
    @ResponseBody
    public String openSyncBizDataMediumTask() {
        openSyncBizDataMediumjJobService.start();
        return "ok";
    }

    @RequestMapping("/syncOrgEmployee")
    @Async
    @ResponseBody
    public String syncOrgEmployee(@RequestParam(required = true) String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        dingtalkIsvEmployeeService.syncOrgEmployee(corpId);
        return "ok";
    }

    @RequestMapping("/syncDepartmentManagers")
    @Async
    @ResponseBody
    public String syncDepartmentManagers(@RequestParam(required = true) String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        dingtalkIsvEmployeeService.syncOrgManagers(corpId);
        return "ok";
    }
}
