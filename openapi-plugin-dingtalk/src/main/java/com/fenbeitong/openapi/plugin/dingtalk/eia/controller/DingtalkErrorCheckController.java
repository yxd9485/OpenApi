package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkErrorCheckService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by lizhen on 2020/11/16.
 */
@Slf4j
@RestController
@RequestMapping("/dingtalk/errorcheck")
@Api(tags = "钉钉错误检查", description = "钉钉错误检查")
public class DingtalkErrorCheckController {

    @Autowired
    private IDingtalkErrorCheckService dingtalkErrorCheckService;

    @RequestMapping("/checkFailedTask")
    public Object checkFailedTask(HttpServletRequest httpRequest) {
        dingtalkErrorCheckService.checkFailedTask();
        return "ok";
    }

    @RequestMapping("/checkFailedTaskAndPhone")
    public Object checkFailedTaskAndPhone(HttpServletRequest httpRequest) throws IOException {
        dingtalkErrorCheckService.checkFailedTaskAndPhone();
        return "ok";
    }

    @RequestMapping("/failOrgList2CsmOrClient")
    public Object failOrgList2CsmOrClient(HttpServletRequest httpRequest) {
        dingtalkErrorCheckService.failOrgList2CsmOrClient();
        return "ok";
    }

}
