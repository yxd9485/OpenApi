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
@RequestMapping("/dingtalk/yida/callback")
@Api(value = "回调", tags = "回调", description = "回调")
@Slf4j
public class YiDaCallbackController {

    @Autowired
    private IYiDaCallbackService yiDaCallbackService;

    @RequestMapping("/receive")
    public Object receive(YiDaCallbackDTO callbackParam) {
        log.info("接收到钉钉回调事件:{}", JsonUtils.toJson(callbackParam));
        yiDaCallbackService.callbackCommand(callbackParam);
        return DingtalkResponseUtils.success(null);
    }
}
