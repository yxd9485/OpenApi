package com.fenbeitong.openapi.plugin.welink.isv.controller;

import com.fenbeitong.openapi.plugin.welink.isv.service.job.WeLinkIsvJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lizhen on 2020/3/28.
 */
@Controller
@Slf4j
@RequestMapping("/welink/isv/job")
public class WeLinkIsvJobController {

    @Autowired
    private WeLinkIsvJobService weLinkIsvJobService;

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public String executeTask() {
        weLinkIsvJobService.start();
        return "ok";
    }
}
