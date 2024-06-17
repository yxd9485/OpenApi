package com.fenbeitong.openapi.plugin.moka.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.moka.dto.JobConfigDto;
import com.fenbeitong.openapi.plugin.moka.service.MokaSyncService;
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
 * <p>Title: FxkSyncController</p>
 * <p>Description: 数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-31 15:45
 */

@Controller
@Slf4j
@RequestMapping("/moka/sync")
public class MokaSyncController {

    @Autowired
    MokaSyncService mokaSyncService;


    /**
     * @Description 组织架构信息同步
     * @Author duhui
     * @Date 2020-08-31
     **/
    @RequestMapping("/syncOrganization")
    @ResponseBody
    public Object syncOrganization(@RequestParam("jobConfig") String jobConfig) {
        JobConfigDto jobConfigDto = JsonUtils.toObj(jobConfig, JobConfigDto.class);
        mokaSyncService.syncOrganization(jobConfigDto);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
