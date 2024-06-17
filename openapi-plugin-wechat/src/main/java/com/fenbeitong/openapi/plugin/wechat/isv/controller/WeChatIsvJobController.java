package com.fenbeitong.openapi.plugin.wechat.isv.controller;

import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvPullThirdOrgService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.job.WeChatIsvJobService;
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
@RequestMapping("/wechat/isv/job")
public class WeChatIsvJobController {

    @Autowired
    private WeChatIsvJobService weChatIsvJobService;

    @Autowired
    private WeChatIsvPullThirdOrgService weChatIsvPullThirdOrgService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @RequestMapping("/execute")
    @Async
    @ResponseBody
    public String executeTask() {
        weChatIsvJobService.start();
        return "ok";
    }


    @RequestMapping("/syncThird")
    @ResponseBody
    public String test(String corpId) {
        weChatIsvPullThirdOrgService.pullThirdOrg(corpId);
        return "ok";
    }

    @RequestMapping("/savePhoneNumToRedis")
    @ResponseBody
    public String savePhoneNumToRedis(String companyId) {
        weChatIsvEmployeeService.savePhoneNumToRedis(companyId);
        return "ok";
    }
}
