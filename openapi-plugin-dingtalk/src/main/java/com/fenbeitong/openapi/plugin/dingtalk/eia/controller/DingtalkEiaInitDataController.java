package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lizhen on 2020/10/30.
 */
@RestController
@RequestMapping("/dingtalk/initdata")
@Api(value = "初始化中间表", tags = "初始化中间表", description = "初始化中间表")
@Slf4j
public class DingtalkEiaInitDataController {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @RequestMapping("/init")
    private Object init(Integer openType, String companyId) {
        openSyncThirdOrgService.init(openType, companyId);
        return "success";
    }

}
