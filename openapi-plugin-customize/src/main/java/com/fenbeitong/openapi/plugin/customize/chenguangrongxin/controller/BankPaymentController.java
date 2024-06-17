package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.IBankPaymentService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName BankPaymentController
 * @Description 辰光融信对公付款
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/20
 **/
@Slf4j
@RestController
@RequestMapping("/customize/bankPayment")
public class BankPaymentController {

    @Autowired
    IBankPaymentService bankPaymentService;

    @RequestMapping("/bankPaymentPush")
    @ApiOperation(value = "对公付款申请单推送", notes = "对公付款申请单推送")
    public Object bankPaymentPush(HttpServletRequest request, @RequestParam(value = "companyId") String companyId) {
        bankPaymentService.pushData(request, companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
