package com.fenbeitong.openapi.plugin.customize.power.controller;


import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.customize.power.service.PowerOrderCallBackService;
import com.fenbeitong.openapi.plugin.customize.power.service.impl.PowerOrderCallBackServiceImpl;
import com.luastar.swift.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangjindong
 */
@RestController
@RequestMapping("/customize/power/pushData")
@Slf4j
public class PowerOrderCallBackController {

    @Autowired
    private PowerOrderCallBackService powerOrderCallBackService;

    @RequestMapping("/orderCallBack/{companyId}")
    @ResponseBody
    public Object orderCallBack(HttpServletRequest request, @PathVariable("companyId") String companyId) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        log.info("活力二八订单回传入参,request={},companyId={}", JsonUtils.toJson(request), companyId);
        return  powerOrderCallBackService.callBackOrderData(requestBody,companyId);
    }
}
