package com.fenbeitong.openapi.plugin.dingtalk.yida.controller;/**
 * <p>Title: YiDaCallbackController</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/13 7:04 下午
 */

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaCallbackDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaCallbackService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaProjectService;
import com.luastar.swift.base.json.JsonUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lizhen on 2021/8/13.
 */
@RestController
@RequestMapping("/dingtalk/yida/job")
@Api(value = "任务", tags = "任务", description = "任务")
@Slf4j
public class YiDaJobController {

    @Autowired
    private IYiDaProjectService yiDaProjectService;

    @RequestMapping("/syncProject")
    public Object receive(String formId, String companyId) {
        yiDaProjectService.syncProject(formId, companyId);
        return DingtalkResponseUtils.success(null);
    }
}
