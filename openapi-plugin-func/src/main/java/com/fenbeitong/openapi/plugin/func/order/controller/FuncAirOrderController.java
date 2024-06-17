package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderExportReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncAirOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncAirOrderController</p>
 * <p>Description: 机票订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/17 4:08 PM
 */
@RestController
@RequestMapping("/func/orders/air")
public class FuncAirOrderController {

    @Autowired
    private FuncAirOrderServiceImpl funcAirOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object listOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        AirOrderListReqDTO req = JsonUtils.toObj(request.getData(), AirOrderListReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcAirOrderService.list(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/detail")
    public Object detail(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        AirOrderDetailReqDTO req = JsonUtils.toObj(request.getData(), AirOrderDetailReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcAirOrderService.detail(req));
    }

    private void checkReq(Object req) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

    @FuncAuthAnnotation
    @RequestMapping("/export")
    public Object exportOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        AirOrderExportReqDTO req = JsonUtils.toObj(request.getData(), AirOrderExportReqDTO.class);
        checkReq(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcAirOrderService.export(req));
    }

}
