package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl.FxkJobServiceImpl;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl.FxkPreInstallDataServiceImpl;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl.FxkPullApprovalServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
@RequestMapping("/fxiaoke/job")
public class FxkJobController {

    @Autowired
    FxkPullApprovalServiceImpl fxkPullApplyService;
    @Autowired
    FxkJobServiceImpl fxkJobService;
    @Autowired
    FxkPreInstallDataServiceImpl fxkPreInstallDataService;

    /**
     * 定时任务拉取纷享销客审批类表数据，并进行入库操作
     *
     * @param jobConfig
     * @return
     */
    @RequestMapping("/syncCarApproval")
    @ResponseBody
    public Object syncCarApproval(@RequestParam("jobConfig") String jobConfig) {
        if (StringUtils.isBlank(jobConfig)) {
            return null;
        }
        Map map = JsonUtils.toObj(jobConfig, Map.class);
        String corpId = (String) map.get("corpId");
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        CompletableFuture.supplyAsync(() -> fxkPullApplyService.pullFxkApprovalData(corpId, 12));
        return FxkResponseUtils.success(Maps.newHashMap());
    }


    /**
     * 定时拉取差旅审批数据
     *
     * @param jobConfig
     * @return
     */
    @RequestMapping("/syncTripApproval")
    @ResponseBody
    public Object syncTripApproval(@RequestParam("jobConfig") String jobConfig) {
        if (StringUtils.isBlank(jobConfig)) {
            return null;
        }
        Map map = JsonUtils.toObj(jobConfig, Map.class);
        String corpId = (String) map.get("corpId");
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        CompletableFuture.supplyAsync(() -> fxkPullApplyService.pullFxkApprovalData(corpId, 1));
        return FxkResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 执行具体的任务，包括差旅和用车
     *
     * @param corpId
     * @return
     */
    @RequestMapping("/executeTask")
    @ResponseBody
    public Object executTask(@RequestParam("corpId") String corpId) {
        CompletableFuture.supplyAsync(() -> fxkJobService.executFxkApply());
        return FxkResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 同步客户列表作为分贝通项目
     *
     * @param jobConfig
     * @return
     */
    @RequestMapping("/syncProject")
    @ResponseBody
    public Object syncProject(@RequestParam("jobConfig") String jobConfig) {
        if (StringUtils.isBlank(jobConfig)) {
            return null;
        }
        Map map = JsonUtils.toObj(jobConfig, Map.class);
        String corpId = (String) map.get("corpId");
        //纷享销客预设对象或者自定义对象的apiName，不同的对象可以设置不同的apiName
        String apiName = (String) map.get("apiName");
        String apiUserId = (String) map.get("apiUserId");
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(apiName) || StringUtils.isBlank(apiUserId)) {
            return null;
        }
        CompletableFuture.supplyAsync(() -> fxkPreInstallDataService.fxkPreInstallDataHandle(corpId, apiName, apiUserId));
        return FxkResponseUtils.success(Maps.newHashMap());
    }

}
