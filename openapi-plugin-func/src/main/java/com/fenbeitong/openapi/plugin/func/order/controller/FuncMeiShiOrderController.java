package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FuncMeiShiOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncMeiShiOrderController</p>
 * <p>Description: 美食订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 4:57 PM
 */
@RestController
@RequestMapping("/func/orders/meishi")
public class FuncMeiShiOrderController {

    @Autowired
    private FuncMeiShiOrderServiceImpl meiShiOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/orderList")
    public Object listOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        String data = request.getData();
        MeiShiOrderListReqDTO req = JsonUtils.toObj(data, MeiShiOrderListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        MeiShiOrderListRespDTO resp = meiShiOrderService.listOrder(req);
        if (resp != null){
          return FuncResponseUtils.success(resp);
        }
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/orderDetail")
    public Object detailOrder(@Valid ApiRequest request) {
        String data = request.getData();
        MeiShiOrderDetailReqDTO req = JsonUtils.toObj(data, MeiShiOrderDetailReqDTO.class);
        checkReq(req);
        MeiShiOrderDTO resp = meiShiOrderService.detailOrder(req.getOrderId());
        return FuncResponseUtils.success(resp);
    }

    private void checkReq(Object req) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/refundList")
    public Object listRefundOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        String data = request.getData();
        MeiShiRefundListReqDTO req = JsonUtils.toObj(data, MeiShiRefundListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        Object resp = meiShiOrderService.listRefundOrder(req);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/refundDetail")
    public Object detailRefund(@Valid ApiRequest request) {
        String data = request.getData();
        MeiShiRefundDetailReqDTO req = JsonUtils.toObj(data, MeiShiRefundDetailReqDTO.class);
        checkReq(req);
        Object resp = meiShiOrderService.detailRefund(req);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/refundList/v1")
    public Object listRefundOrderV1(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        String data = request.getData();
        MeiShiRefundListReqDTO req = JsonUtils.toObj(data, MeiShiRefundListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setApiVersion("v_1.0");
        Object resp = meiShiOrderService.listRefundOrder(req);
        if (resp == null){
            return FuncResponseUtils.success(Maps.newHashMap());
        }

        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/refundDetail/v1")
    public Object detailRefundV1(@Valid ApiRequest request) {
        String data = request.getData();
        MeiShiRefundDetailReqDTO req = JsonUtils.toObj(data, MeiShiRefundDetailReqDTO.class);
        checkReq(req);
        req.setApiVersion("v_1.0");
        Object resp = meiShiOrderService.detailRefund(req);
        return FuncResponseUtils.success(resp);
    }
}
