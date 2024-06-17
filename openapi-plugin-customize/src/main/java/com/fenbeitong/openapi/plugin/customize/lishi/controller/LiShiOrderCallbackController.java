package com.fenbeitong.openapi.plugin.customize.lishi.controller;

import com.fenbeitong.openapi.plugin.customize.lishi.service.ILiShiOrderCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: LiShiOrderCallbackController</p>
 * <p>Description: 理士订单回传</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/11 11:35 AM
 */
@RestController
@RequestMapping("/customize/lishi/order")
public class LiShiOrderCallbackController {

    @Autowired
    private ILiShiOrderCallbackService orderCallbackService;

    @RequestMapping("/callback/{configId}")
    public Object callback(@PathVariable("configId") Long configId, @RequestBody String data) {
        return orderCallbackService.callback(configId, data);
    }
}
