package com.fenbeitong.openapi.plugin.beisen.standard.controller;

import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenResponseCode;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenJobParamDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenResultEntity;
import com.fenbeitong.openapi.plugin.beisen.common.util.BeisenResponseUtils;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenPullDataService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenPullDataServiceV2;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenSyncOrgByVersionTemplateService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenResponseCode.BEISEN_JOB_PARAM_ERROR;

/**
 * 拉取企业微信人员组织架构的定时任务
 * Created by Z.H.W on 20/02/18.
 */
@Controller
@Slf4j
@RequestMapping("/beisen/pullThirdOrg")
public class BeiSenPullThirdOrgController {
    @Autowired
    private BeisenPullDataService beisenPullDataService;
    @Autowired
    private BeisenPullDataServiceV2 beisenPullDataServiceV2;
    @Autowired
    private BeisenSyncOrgByVersionTemplateService syncOrgByVersionTemplateService;


    /**
     * 全量拉取北森数据
     *
     * @param jobConfig parentId  startTime companyId
     * @param
     * @return
     */
    @RequestMapping("/fullAllData")
    @ResponseBody
    public Object syncPullAllData(@RequestParam("jobConfig") String jobConfig) {
        BeisenParamConfig beisenParamConfig = JsonUtils.toObj(jobConfig, BeisenParamConfig.class);
        beisenPullDataService.pullAllData(beisenParamConfig);
        return BeisenResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 增量拉取北森数据
     *
     * @param jobConfig parentId  hours companyId
     * @param
     * @return
     */
    @RequestMapping("/fullIncrementalData")
    @ResponseBody
    @Deprecated
    public Object syncPullIncrementalData(@RequestParam("jobConfig") String jobConfig) {
        BeisenParamConfig beisenParamConfig = JsonUtils.toObj(jobConfig, BeisenParamConfig.class);
        return BeisenResponseUtils.success(beisenPullDataService.pullIncrementalData(beisenParamConfig));
    }


    /**
     * 全量拉取北森数据
     *
     * @param jobConfig parentId  startTime companyId
     * @param
     * @return
     */
    @RequestMapping("/v2/fullAllData")
    @ResponseBody
    public Object syncPullAllDataV2(@RequestParam("jobConfig") String jobConfig) {
        BeisenParamConfig beisenParamConfig = JsonUtils.toObj(jobConfig, BeisenParamConfig.class);
        beisenPullDataServiceV2.pullAllDataV2(beisenParamConfig);
        return BeisenResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 根据版本全量同步北森部门和人员信息
     *
     * @return 结果
     */
    @RequestMapping("/syncEmployeeAndDeptByVersion")
    @ResponseBody
    public BeisenResultEntity syncEmployeeAndDeptByVersion(@RequestBody BeisenJobParamDTO jobParamDTO) {
        syncOrgByVersionTemplateService.doSyncEmployeeAndDept(jobParamDTO);
        return BeisenResponseUtils.success(Maps.newHashMap());
    }

}
