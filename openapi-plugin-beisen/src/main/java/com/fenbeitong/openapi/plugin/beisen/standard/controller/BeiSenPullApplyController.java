package com.fenbeitong.openapi.plugin.beisen.standard.controller;


import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.util.BeisenResponseUtils;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenApplyPullDataService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xiaowei
 * @date 2020/7/28
 */
@Controller
@Slf4j
@RequestMapping("/beisen/apply")
public class BeiSenPullApplyController {

     @Autowired
     private BeisenApplyPullDataService beisenApplyPullDataService;


    @RequestMapping("/execute")
    @ResponseBody
    public Object pullApplyData(@RequestParam("jobConfig") String jobConfig) {
        BeisenParamConfig beisenParamConfig = JsonUtils.toObj(jobConfig, BeisenParamConfig.class);
        if (beisenApplyPullDataService.pullApplyData(beisenParamConfig)) {
            return BeisenResponseUtils.success("success");
        } else {
            return BeisenResponseUtils.success("failure");
        }
    }
}
