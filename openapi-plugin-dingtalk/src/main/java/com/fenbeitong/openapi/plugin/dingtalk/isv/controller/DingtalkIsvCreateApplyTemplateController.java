package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl.DingtalkIsvCreateApplyTemplateServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description 模板创建
 * @Author duhui
 * @Date 2021-04-09
 **/
@Controller
@Slf4j
@RequestMapping("/dingtalk/isv/create")
public class DingtalkIsvCreateApplyTemplateController {


    @Autowired
    DingtalkIsvCreateApplyTemplateServiceImpl dingtalkIsvCreateApplyTemplateService;

    @RequestMapping("/template")
    @ResponseBody
    public Object receive(String companyId, String companyName, String templateName) {
        return DingtalkResponseUtils.success(dingtalkIsvCreateApplyTemplateService.createApplyTemplate(companyId, companyName, templateName));
    }
}
