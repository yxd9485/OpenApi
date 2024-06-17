package com.fenbeitong.openapi.plugin.customize.archive.yihai.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.archive.yihai.dto.YiHaiConfigDTO;
import com.fenbeitong.openapi.plugin.customize.archive.yihai.service.ArchiveProjectService;
import com.fenbeitong.openapi.plugin.support.archive.dto.OpenThirdArchiveDTO;
import com.fenbeitong.openapi.plugin.support.archive.service.OpenThirdArchiveService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: TalkOrganizationController</p>
 * <p>Description: 颐海档案项目同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:28
 */

@Controller
@Slf4j
@RequestMapping("/yihai/sync")
public class YiHaiArchiveController {

    @Autowired
    ArchiveProjectService archiveProjectService;

    @Autowired
    OpenThirdArchiveService openThirdArchiveService;


    /**
     * @Description 全量颐海档案项目同步
     * @Author duhui
     * @Date 2021-05-17
     **/
    @RequestMapping("/archiveProjiecAll")
    @ResponseBody
    public Object archiveProjiecAll(@RequestParam("jobConfig") String jobConfig) {
        YiHaiConfigDTO basicAuth = JsonUtils.toObj(jobConfig, YiHaiConfigDTO.class);
        CompletableFuture.supplyAsync(() -> archiveProjectService.ArchiveProjectSyncAll(basicAuth)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());

    }


    /**
     * @Description 增量颐海档案项目同步
     * @Author duhui
     * @Date 2021-05-17
     **/
    @RequestMapping("/archiveProjiecPart")
    @ResponseBody
    public Object archiveProjiecPart(@RequestParam("jobConfig") String jobConfig) {
        YiHaiConfigDTO basicAuth = JsonUtils.toObj(jobConfig, YiHaiConfigDTO.class);
        CompletableFuture.supplyAsync(() -> archiveProjectService.ArchiveProjectSyncPart(basicAuth)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());

    }


    /**
     * @Description 创建档案
     * @Author duhui
     * @Date 2021-05-18
     **/
    @RequestMapping("/createArchive")
    @ResponseBody
    public Object createArchive(@RequestParam("jobConfig") String jobConfig) {
        OpenThirdArchiveDTO openThirdArchiveDTO = JsonUtils.toObj(jobConfig, OpenThirdArchiveDTO.class);
        CompletableFuture.supplyAsync(() -> openThirdArchiveService.create(openThirdArchiveDTO)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());

    }

}
