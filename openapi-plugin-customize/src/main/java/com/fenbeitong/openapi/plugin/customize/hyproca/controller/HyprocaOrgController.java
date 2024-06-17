package com.fenbeitong.openapi.plugin.customize.hyproca.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.hyproca.service.HyprocaOrgService;
import com.fenbeitong.openapi.plugin.support.organization.dto.JobConfigDto;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: TalkOrganizationController</p>
 * <p>Description: 海普诺凯组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:28
 */

@Controller
@Slf4j
@RequestMapping("/hyproca/sync")
public class HyprocaOrgController {

    @Autowired
    HyprocaOrgService hyprocaOrgService;


    /**
     * @Description 组织架构信息全量同步
     * @Author duhui
     * @Date 2020-08-31
     **/
    @RequestMapping("/allSyncOrganization")
    @ResponseBody
    public Object syncOrganization(@RequestParam("jobConfig") String jobConfig) {
        JobConfigDto jobConfigDto = JsonUtils.toObj(jobConfig, JobConfigDto.class);
        CompletableFuture.supplyAsync(() -> hyprocaOrgService.allSync(jobConfigDto.getCompanyId(), jobConfigDto.getTopId())).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    /**
     * @Description 组织架构增量同步(作废)
     * @Author duhui
     * @Date 2020-12-08
     **/
    @RequestMapping("/syncOrganizationProtion/{companyId}")
    @ResponseBody
    public Object syncOrganizationProtion(@PathVariable("companyId") String companyId) {
        CompletableFuture.supplyAsync(() -> hyprocaOrgService.syncOrganizationProtion(companyId)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


}
