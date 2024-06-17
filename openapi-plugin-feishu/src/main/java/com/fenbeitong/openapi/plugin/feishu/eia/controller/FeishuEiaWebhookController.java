package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.feishu.common.service.FeishuWorkrecordService;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @ClassName FeishuEiaWebhookController
 * @Description 飞书内嵌版webhook推送
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/12/8 下午2:01
 **/
@Controller
@Slf4j
@RequestMapping("/feishu/webhook")
public class FeishuEiaWebhookController {

    @Autowired
    private FeishuWorkrecordService workrecordService;

    @RequestMapping("/complete/push")
    @ResponseBody
    public Object webhookPush(@RequestBody Map commonRecord){
        workrecordService.pushWOrkrecodInfo( commonRecord );
        return FeiShuResponseUtils.success(Maps.newHashMap());
    }
}
