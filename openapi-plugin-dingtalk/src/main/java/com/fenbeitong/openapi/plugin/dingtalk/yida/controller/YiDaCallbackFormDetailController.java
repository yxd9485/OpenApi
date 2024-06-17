package com.fenbeitong.openapi.plugin.dingtalk.yida.controller;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaFormDetailDispatchService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 宜搭表单回调
 *
 * @author ctl
 * @date 2022/3/3
 */
@RestController
@RequestMapping("/dingtalk/yida/callback/form/detail")
@Slf4j
public class YiDaCallbackFormDetailController {

    @Autowired
    private IYiDaFormDetailDispatchService iYiDaFormDetailDispatchService;

    /**
     * 接收宜搭表单详情回调
     *
     * @param params
     * @return
     */
    @PostMapping("/receive")
    public Object receive(@RequestParam Map<String, Object> params) {
        log.info("接收到宜搭表单:{}", JsonUtils.toJson(params));
        iYiDaFormDetailDispatchService.dispatch(params);
        log.info("宜搭表单同步成功");
        return DingtalkResponseUtils.success(null);
    }

}
