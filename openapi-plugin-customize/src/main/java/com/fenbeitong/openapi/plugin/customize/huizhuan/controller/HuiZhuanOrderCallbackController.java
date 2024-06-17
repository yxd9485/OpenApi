package com.fenbeitong.openapi.plugin.customize.huizhuan.controller;


import com.fenbeitong.openapi.plugin.customize.huizhuan.service.HuiZhuanOrderCallbackService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 汇专订单回传定制
 * @author lizhen
 */
@RestController
@Slf4j
@RequestMapping("/customize/huizhuan/order")
public class HuiZhuanOrderCallbackController {
    @Autowired
    private HuiZhuanOrderCallbackService huiZhuanOrderCallbackService;

    @PostMapping("/callback")
    public Object pushReimburseBill(@RequestBody String data, @RequestParam(required = true) String companyId) {
        return huiZhuanOrderCallbackService.callback(data, companyId);
    }
}
