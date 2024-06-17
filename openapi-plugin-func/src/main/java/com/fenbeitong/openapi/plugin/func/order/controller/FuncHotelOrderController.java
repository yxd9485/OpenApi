package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FuncExpressOrderServiceImpl;
import com.fenbeitong.openapi.plugin.func.order.service.FuncHotelOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncHotelOrderController</p>
 * <p>Description: 酒店订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/15 2:37 PM
 */
@RestController
@RequestMapping("/func/orders/hotel")
public class FuncHotelOrderController {

    @Autowired
    private FuncHotelOrderServiceImpl hotelOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        HotelOrderListReqDTO req = JsonUtils.toObj(request.getData(), HotelOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new HotelOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(hotelOrderService.list(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/detail")
    public Object detail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        OrderDetailReqDTO req = JsonUtils.toObj(request.getData(), OrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(hotelOrderService.detail(req));
    }





}
