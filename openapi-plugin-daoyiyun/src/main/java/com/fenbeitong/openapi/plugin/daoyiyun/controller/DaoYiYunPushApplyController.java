package com.fenbeitong.openapi.plugin.daoyiyun.controller;

import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunApplyService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 单据推送
 * @author lizhen
 */
@RestController
@Slf4j
@RequestMapping("/daoyiyun/apply")
public class DaoYiYunPushApplyController {

    @Autowired
    private DaoYiYunApplyService daoYiYunApplyService;

    @RequestMapping("/createApplyInstance")
    public Object createApplyInstance(@RequestBody String body, String applicationId, String formModelId) {
        String revice = daoYiYunApplyService.createApplyInstance(body, applicationId, formModelId);
        return revice;
    }
}
