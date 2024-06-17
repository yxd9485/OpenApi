package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.wechat.eia.service.job.WeChatEiaJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 执行创建的企业微信定时任务
 * Created by dave.hansins on 19/12/13.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/job")
public class WeChatEiaJobController {

    @Autowired
    WeChatEiaJobService weChatEiaJobService;

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public String executeTask() {
        weChatEiaJobService.start();
        return "ok";
    }
}
