package com.fenbeitong.openapi.plugin.kingdee.customize.yuntian.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuntian.service.YunTianToJinDieService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: YunTianToJinDieController</p>
 * <p>Description:  云天励飞-对接金蝶</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-26 11:29
 */
@Slf4j
@RestController
@RequestMapping("/customize/yunTianToJinDie/task")
public class YunTianToJinDieController {

    @Autowired
    YunTianToJinDieService yunTianToJinDieService;

    @RequestMapping("/syncProject/{companyId}")
    public Object syncIteam(@PathVariable("companyId") String companyId) {
        CompletableFuture.supplyAsync(() -> yunTianToJinDieService.syncProject(companyId)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());

    }
}
