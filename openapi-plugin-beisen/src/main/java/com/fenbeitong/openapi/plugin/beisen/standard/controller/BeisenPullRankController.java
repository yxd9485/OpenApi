package com.fenbeitong.openapi.plugin.beisen.standard.controller;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenJobParamDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenRankParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenResultEntity;
import com.fenbeitong.openapi.plugin.beisen.common.util.BeisenResponseUtils;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenPullDataService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenSyncOrgByVersionTemplateService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName BeisenPullRankController
 * @Description 拉取北森职级定时任务
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/17
 **/
@RestController
@Slf4j
@RequestMapping("/beisen/pullRank")
public class BeisenPullRankController {
    @Autowired
    private BeisenPullDataService beisenPullDataService;
    @Autowired
    private BeisenSyncOrgByVersionTemplateService syncOrgByVersionTemplateService;

    /**
     * 全量拉取北森职级数据
     *
     * @param beisenRankParamConfig parentId  startTime companyId
     *
     */
    @RequestMapping("/syncAllRank")
    public Object syncAllRank(@RequestBody BeisenRankParamConfig beisenRankParamConfig) {
        beisenPullDataService.syncAllRank(beisenRankParamConfig);
        return BeisenResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 全量拉取北森职级信息
     *
     * @return 结果
     */
    @RequestMapping("/syncRankByVersion")
    public BeisenResultEntity syncRankByVersion(@RequestBody BeisenJobParamDTO jobParamDTO) {
        syncOrgByVersionTemplateService.doSyncRank(jobParamDTO);
        return BeisenResponseUtils.success(Maps.newHashMap());
    }

}
