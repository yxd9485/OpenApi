package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FuncMallOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncMallOrderController</p>
 * <p>Description: 采购订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 3:56 PM
 */
@RestController
@RequestMapping("/func/orders/mall")
public class FuncMallOrderController {

    @Autowired
    private FuncMallOrderServiceImpl mallOrderService;
    @Autowired
    private CommonAuthService signService;

    @SuppressWarnings("all")
    @ApiOperation(value = "采购订单列表", notes = "采购订单列表", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/orderList")
    public Object listOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        String data = request.getData();
        MallOrderListReqDTO req = JsonUtils.toObj(data, MallOrderListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        Object resp = mallOrderService.listOrder(req);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @ApiOperation(value = "采购订单详情", notes = "采购订单详情", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/orderDetail")
    public Object detailOrder(@Valid ApiRequest request) {
        String data = request.getData();
        MallOrderDetailReqDTO req = JsonUtils.toObj(data, MallOrderDetailReqDTO.class);
        checkReq(req);
        String appId = signService.getAppId(request);
        Object resp = mallOrderService.detailOrder(req.getOrderId(),appId);
        return FuncResponseUtils.success(resp);
    }

    private void checkReq(Object req) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

    @SuppressWarnings("all")
    @ApiOperation(value = "采购售后单列表", notes = "采购售后单列表", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/refundList")
    public Object listRefundOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        String data = request.getData();
        MallRefundListReqDTO req = JsonUtils.toObj(data, MallRefundListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        Object resp = mallOrderService.listRefundOrder(req);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @ApiOperation(value = "采购售后单详情", notes = "采购售后单详情", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/refundDetail")
    public Object detailRefund(@Valid ApiRequest request) {
        String data = request.getData();
        MallRefundDetailReqDTO req = JsonUtils.toObj(data, MallRefundDetailReqDTO.class);
        checkReq(req);
        String appId = signService.getAppId(request);
        Object resp = mallOrderService.detailRefund(req,appId);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @ApiOperation(value = "采购售后单列表", notes = "采购售后单列表", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/refundList/v1")
    public Object listRefundOrderV1(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        String data = request.getData();
        MallRefundListReqDTO req = JsonUtils.toObj(data, MallRefundListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setApiVersion("v_1.0");
        Object resp = mallOrderService.listRefundOrder(req);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @ApiOperation(value = "采购售后单详情", notes = "采购售后单详情", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/refundDetail/v1")
    public Object detailRefundV1(@Valid ApiRequest request) {
        String data = request.getData();
        MallRefundDetailReqDTO req = JsonUtils.toObj(data, MallRefundDetailReqDTO.class);
        checkReq(req);
        req.setApiVersion("v_1.0");
        String appId = signService.getAppId(request);
        Object resp = mallOrderService.detailRefund(req,appId);
        return FuncResponseUtils.success(resp);
    }
}
