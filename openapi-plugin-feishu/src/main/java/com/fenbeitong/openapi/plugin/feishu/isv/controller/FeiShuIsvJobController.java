package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.job.FeiShuIsvJobService;
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
@RequestMapping("/feishu/isv/job")
public class FeiShuIsvJobController {

    @Autowired
    private FeiShuIsvJobService feiShuIsvJobService;

    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;
    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public String executeTask() {
        feiShuIsvJobService.start();
        return "ok";
    }


    @RequestMapping("/syncOrgEmployee")
    @Async
    @ResponseBody
    public String syncOrgEmployee(@RequestParam(required = true) String corpId) {
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String companyId = feiShuIsvCompany.getCompanyId();
        feiShuIsvEmployeeService.syncFeiShuIsvOrgEmployee(corpId, companyId);
        return "ok";
    }


    @RequestMapping("/syncOrgManagers")
    @Async
    @ResponseBody
    public String syncOrgManagers(@RequestParam(required = true) String corpId) {
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String companyId = feiShuIsvCompany.getCompanyId();
        feiShuIsvEmployeeService.syncFeiShuIsvOrgManagers(corpId,companyId);
        return "ok";
    }

}
