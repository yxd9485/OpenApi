package com.fenbeitong.openapi.plugin.customize.shankun.controller;

import com.fenbeitong.openapi.plugin.customize.shankun.service.IShangKunOrderCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ShangKunOrderCallbackController</p>
 * <p>Description: 上坤订单回调</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/26 5:58 PM
 */
@RestController
@RequestMapping("/customize/shangkun/order")
public class ShangKunOrderCallbackController {

    @Autowired
    private IShangKunOrderCallbackService shangKunOrderCallbackService;

    @RequestMapping("/callback")
    public Object callback(@RequestParam("url") String url, @RequestBody String data) {
        return shangKunOrderCallbackService.callback(url, data);
    }
}
