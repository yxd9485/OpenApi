package com.fenbeitong.openapi.plugin.kingdee.common.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeProjectSyncService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 金蝶项目同步通用接口
 * @Auther zhang.peng
 * @Date 2021/6/3
 */
@Slf4j
@RestController
@RequestMapping("/kingdee/3kCloud/sync")
public class KingdeeSyncProjectCommonController {

    @Autowired
    KingDeeProjectSyncService jinDieProjectSyncService;

    @RequestMapping("/project/{companyId}")
    public Object syncProject(@PathVariable("companyId") String companyId ){

        CompletableFuture.supplyAsync(() -> jinDieProjectSyncService.syncItem(companyId)).exceptionally( e -> {
                log.warn("同步项目失败 {} , companyId {} " , e.getMessage() , companyId);
                return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
