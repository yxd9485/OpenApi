package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.CarOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncCarOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
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
@RequestMapping("/func/orders/car")
public class FuncCarOrderController {

    @Autowired
    private FuncCarOrderServiceImpl carOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        CarOrderListReqDTO req = JsonUtils.toObj(request.getData(), CarOrderListReqDTO.class);
        if(!ObjectUtils.isEmpty(req)){
            ValidatorUtils.validateBySpring(req);
            req.setCompanyId((String) httpRequest.getAttribute("companyId"));
            return FuncResponseUtils.success(carOrderService.list(req));
        }else{
           return FuncResponseUtils.success(Maps.newHashMap());
        }
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/detail")
    public Object detail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        OrderDetailReqDTO req = JsonUtils.toObj(request.getData(), OrderDetailReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new OrderDetailReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(carOrderService.detail(req));
    }


}
