package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.NingBoWeiLiProjectSycnService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 宁波伟立同步项目数据
 * @Auther zhang.peng
 * @Date 2021/5/18
 */
@Slf4j
@RestController
@RequestMapping("/customize/ningboweili/sync")
public class NingBoWeiLiProjectSyncController {

    @Autowired
    private NingBoWeiLiProjectSycnService ningBoWeiLiProjectSycnService;

    @RequestMapping("/project/{companyId}")
    public Object syncProjectInfo(@PathVariable("companyId") String companyId){
        CompletableFuture.supplyAsync(() -> ningBoWeiLiProjectSycnService.sycn(companyId)).exceptionally( e-> {
            log.warn("宁波伟立同步项目失败 : {}",e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
