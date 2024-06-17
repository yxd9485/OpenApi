package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.core.util.ApplicationContextUtils;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.job.YunzhijiaJobService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.stream.Collectors;


/**
 * 一个插件使用一个job进行任务调度，其中包含通讯录和审批两种任务，通讯录具体包含部门和人员的分拆事件
 * 不同插件之间的任务执行分开执行，相互不影响
 */
@Controller
@Slf4j
@RequestMapping("/yunzhijia/job")
public class YunzhijiaJobController {
    @Autowired
    YunzhijiaJobService yunzhijiaJobService;

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public String executeTask() {
        yunzhijiaJobService.start();
        return "ok";
    }

}
