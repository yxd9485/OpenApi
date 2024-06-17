package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.sync.YunzhijiaSyncService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/yunzhijia/orgEmp")
public class YunzhijiaSyncController {

    @Autowired
    YunzhijiaSyncService yunzhijiaSyncService;

    @RequestMapping("/syncThird")
    @ResponseBody
    public Object syncThird(@RequestParam("companyId") String companyId) {
        CompletableFuture.supplyAsync(() -> yunzhijiaSyncService.syncOrgEmployee(companyId)).exceptionally(e -> {
            log.error("{}", e);
            return "false";
        });
        return YunzhijiaResponseUtils.success(Maps.newHashMap());
    }
}
