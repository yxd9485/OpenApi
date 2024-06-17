package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.BankOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.PublicPaymentOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncBankOrderServiceImpl;
import com.fenbeitong.openapi.plugin.func.order.service.FuncPublicPaymentOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncPublicPaymentOrderController</p>
 * <p>Description: 对公交易订单信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/8/31 10:37 AM
 */
@RestController
@RequestMapping("/func/orders/public/payment")
public class FuncPublicPaymentOrderController {

    @Autowired
    private FuncPublicPaymentOrderServiceImpl funcPublicPaymentOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        PublicPaymentOrderListReqDTO req = JsonUtils.toObj(request.getData(), PublicPaymentOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new PublicPaymentOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcPublicPaymentOrderService.list(req));
    }



}
