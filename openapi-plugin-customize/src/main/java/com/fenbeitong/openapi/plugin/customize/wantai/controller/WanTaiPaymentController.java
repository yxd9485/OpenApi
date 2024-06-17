package com.fenbeitong.openapi.plugin.customize.wantai.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiPaymentService;
import com.fenbeitong.openapi.plugin.func.annotation.SecurityAnnotation;
import com.fenbeitong.openapi.plugin.func.callback.dto.PaymentRecordDTO;
import com.fenbeitong.openapi.plugin.func.common.OpenApiConstant;
import com.fenbeitong.openapi.plugin.support.payment.dto.PaymentCustomReqDTO;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 万泰-付款单
 *
 * @author machao
 * @date 2022/9/28
 */
@RestController
@RequestMapping("/gate/customize/wantai/payment")
public class WanTaiPaymentController {

    @Autowired
    private WanTaiPaymentService wanTaiPaymentService;

    /**
     * 万泰定制-付款单创建
     */
    @SecurityAnnotation
    @RequestMapping("/create")
    public Object createPayment(@RequestBody PaymentCustomReqDTO req, HttpServletRequest request) {
        req.setCompanyId(request.getAttribute(OpenApiConstant.COMPANY_ID).toString());
        return OpenapiResponseUtils.success(wanTaiPaymentService.createPaymentCustom(req));
    }
}
