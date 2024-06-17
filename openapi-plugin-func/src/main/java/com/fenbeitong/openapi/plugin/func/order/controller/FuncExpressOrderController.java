package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FuncExpressOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * <p>Title: FuncExpressOrderController</p>
 * <p>Description: 快递闪送订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/3 2:37 PM
 */
@RestController
@RequestMapping("/func/orders/express")
public class FuncExpressOrderController {

    @Autowired
    private FuncExpressOrderServiceImpl expressOrderService;
    @Autowired
    private CommonAuthService signService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/deliveryOrderList")
    public Object listDeliveryOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        ExpressDeliveryOrderListReqDTO req = JsonUtils.toObj(request.getData(), ExpressDeliveryOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new ExpressDeliveryOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(expressOrderService.deliveryListOrder(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/flashSendOrderList")
    public Object listFlashSendOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        ExpressFlashSendOrderListReqDTO req = JsonUtils.toObj(request.getData(), ExpressFlashSendOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new ExpressFlashSendOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(expressOrderService.flashSendListOrder(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/deliveryOrderDetail")
    public Object deliveryDetailOrder(@Valid ApiRequest request) throws BindException {
        ExpressOrderDetailReqDTO req = JsonUtils.toObj(request.getData(), ExpressOrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        String appId = signService.getAppId(request);
        return FuncResponseUtils.success(expressOrderService.deliveryDetailOrder(req,appId));
    }


    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/flashSendOrderDetail")
    public Object flashSendDetailOrder(@Valid ApiRequest request) throws BindException {
        ExpressOrderDetailReqDTO req = JsonUtils.toObj(request.getData(), ExpressOrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        String appId = signService.getAppId(request);
        return FuncResponseUtils.success(expressOrderService.flashSendDetailOrder(req,appId));
    }


}
