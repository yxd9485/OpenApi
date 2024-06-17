package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.YunzhijiaPullOrgService;
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
@RequestMapping("/yunzhijia/pullThirdOrg")
public class YunzhijiaPullOrgController {
    @Autowired
    YunzhijiaPullOrgService yunzhijiaPullOrgService;

    @RequestMapping("/syncThird")
    @ResponseBody
    public Object syncThird(@RequestParam("corpId") String corpId) {
        String deptId="";
        CompletableFuture.supplyAsync(() -> yunzhijiaPullOrgService.pullThirdOrg(corpId, deptId)).exceptionally(e -> {
            log.warn("{}", e);
            return "false";
        });
        return YunzhijiaResponseUtils.success(Maps.newHashMap());
    }
}
