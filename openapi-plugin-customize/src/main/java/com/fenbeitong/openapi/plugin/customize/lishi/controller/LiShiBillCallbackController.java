package com.fenbeitong.openapi.plugin.customize.lishi.controller;

import com.fenbeitong.openapi.plugin.customize.lishi.service.ILiShiBillCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: LiShiBillCallbackController</p>
 * <p>Description: 理士帐单回传</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/6/03
 */
@RestController
@RequestMapping("/customize/lishi/bill")
public class LiShiBillCallbackController {

    @Autowired
    private ILiShiBillCallbackService billCallbackService;

    @RequestMapping("/callback/{configId}/{userName}/{password}/{param}")
    public Object callback(@PathVariable("configId") Long configId, @PathVariable("userName") String userName, @PathVariable("password") String password, @PathVariable("param") String param, @RequestBody String data) {
        return billCallbackService.callback(configId, data, userName, password, param);
    }
}
