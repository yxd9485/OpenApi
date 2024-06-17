package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.KingDeeK3CloudService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: YunTianToJinDieController</p>
 * <p>Description:  金蝶k/3cloud 组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-10-14 11:29
 */

@Slf4j
@RestController
@RequestMapping("/customize/jindie/3kCloud")
public class KingDeeK3CloudController {

    @Autowired
    KingDeeK3CloudService jinDie3kCloudService;

    @RequestMapping("/syncOrganization/{companyId}")
    public Object syncIteam(@PathVariable("companyId") String companyId) {
        CompletableFuture.supplyAsync(() -> jinDie3kCloudService.syncOrganization(companyId)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
