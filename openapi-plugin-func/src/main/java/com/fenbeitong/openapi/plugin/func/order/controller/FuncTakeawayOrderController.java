package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.HotelOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.RefundOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.TakeawayOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncHotelOrderServiceImpl;
import com.fenbeitong.openapi.plugin.func.order.service.FuncTakeawayOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncTakeawayOrderController</p>
 * <p>Description: 外卖订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/18 6:37 PM
 */
@RestController
@RequestMapping("/func/orders/takeaway")
public class FuncTakeawayOrderController {

    @Autowired
    private FuncTakeawayOrderServiceImpl takeawayOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        TakeawayOrderListReqDTO req = JsonUtils.toObj(request.getData(), TakeawayOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new TakeawayOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(takeawayOrderService.list(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/detail")
    public Object detail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        OrderDetailReqDTO req = JsonUtils.toObj(request.getData(), OrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(takeawayOrderService.detail(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/refundList")
    public Object reundList(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        TakeawayOrderListReqDTO req = JsonUtils.toObj(request.getData(), TakeawayOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new TakeawayOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(takeawayOrderService.refundList(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/refundDetail")
    public Object refundDetail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        RefundOrderDetailReqDTO req = JsonUtils.toObj(request.getData(), RefundOrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(takeawayOrderService.refundDetail(req));
    }

}
