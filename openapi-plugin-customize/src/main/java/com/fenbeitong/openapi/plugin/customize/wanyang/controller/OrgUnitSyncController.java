package com.fenbeitong.openapi.plugin.customize.wanyang.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wanyang.service.impl.OrgUnitSyncServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName OrgUnitSyncController
 * @Description 万洋组织架构对接
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/8/2 下午6:24
 **/
@RestController
@Slf4j
@RequestMapping("/wanyang/customize")
public class OrgUnitSyncController {

    @Autowired
    private OrgUnitSyncServiceImpl orgUnitSyncService;

    @RequestMapping("/orgUnit/{companyId}")
    @ResponseBody
    @Async
    public Object syncOrgUnit(@PathVariable("companyId") String companyId){
        orgUnitSyncService.syncOrgUnit(companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
