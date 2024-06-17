package com.fenbeitong.openapi.plugin.beisen.standard.controller;

import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenOutwardApplyJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lizhen
 * @date 2020/6/12
 */
@Controller
@Slf4j
@RequestMapping("/beisen/outwardApply/job")
public class BeisenOutwardApplyCreateJobController {

    @Autowired
    private BeisenOutwardApplyJobService beisenOutwardApplyJobService;

    @RequestMapping("/execute")
    @ResponseBody
    public String executeTask() {
        beisenOutwardApplyJobService.start();
        return "ok";
    }
}
