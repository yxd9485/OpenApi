package com.fenbeitong.openapi.plugin.customize.hyproca.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.hyproca.dto.HyprocaJobConfigDto;
import com.fenbeitong.openapi.plugin.customize.hyproca.service.HyprocaTripApplyPullService;
import com.fenbeitong.openapi.plugin.customize.hyproca.service.impl.HyprocaTripApplyJobServiceImpl;
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
 * <p>Description: 海普诺凯审批单同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:28
 */

@Controller
@Slf4j
@RequestMapping("/hyproca/tripApply/sync")
public class HyprocaTripApplyController {
    @Autowired
    HyprocaTripApplyJobServiceImpl hyprocaTripApplyJobService;

    @Autowired
    HyprocaTripApplyPullService hyprocaTripApplyPullService;


    /**
     * 审批单同步执行
     */
    @RequestMapping("/pull")
    @ResponseBody
    public Object tripApplyPull(@RequestParam("jobConfig") String jobConfig) {
        HyprocaJobConfigDto HyprocaJobConfigDto = JsonUtils.toObj(jobConfig, HyprocaJobConfigDto.class);
        CompletableFuture.supplyAsync(() -> hyprocaTripApplyPullService.tripApplyPull(HyprocaJobConfigDto)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 审批单同步执行
     */
    @RequestMapping("/execute")
    @ResponseBody
    public Object executeTask() {
        hyprocaTripApplyJobService.start();
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


}
