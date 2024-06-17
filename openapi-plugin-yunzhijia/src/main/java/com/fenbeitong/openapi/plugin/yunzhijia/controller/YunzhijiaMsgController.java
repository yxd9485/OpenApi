package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.notice.sender.YunzhijiaNoticeSender;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/yunzhijia/msg")
public class YunzhijiaMsgController {
    @Autowired
    YunzhijiaNoticeSender yunzhijiaNoticeSender;

    @RequestMapping("/send")
    @ResponseBody
    public Object getYunzhijiaApplyDetail(String corpId,String employeeId,String msg) {
        yunzhijiaNoticeSender.sender(corpId,employeeId,msg);
        return YunzhijiaResponseUtils.success(Maps.newHashMap());
    }
}
