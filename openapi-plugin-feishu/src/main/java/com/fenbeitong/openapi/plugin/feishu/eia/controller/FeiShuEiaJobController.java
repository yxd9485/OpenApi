package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.eia.service.job.FeiShuEiaJobService;

import com.google.common.collect.Maps;
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
@RequestMapping("/feishu/eia/job")
public class FeiShuEiaJobController {

    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;

    @Autowired
    private FeiShuEiaJobService feiShuEiaJobService;

    @RequestMapping("/syncOrgEmployee")
//    @Async
    @ResponseBody
    public Object syncOrgEmployee(@RequestParam("companyId") String companyId) {
        feiShuEiaEmployeeService.syncFeiShuEiaOrgEmployee(companyId);
        return FeiShuResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public Object executeTask() {
        feiShuEiaJobService.start();
        return FeiShuResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/syncOrgManagers")
    @ResponseBody
    public Object syncOrgManagers(@RequestParam("companyId") String companyId) {
        feiShuEiaEmployeeService.syncThirdOrgManagers(companyId);
        return FeiShuResponseUtils.success(Maps.newHashMap());
    }
}
