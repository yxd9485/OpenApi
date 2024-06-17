package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FuncExpressOrderServiceImpl;
import com.fenbeitong.openapi.plugin.func.order.service.FuncReliefOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncReliefOrderController</p>
 * <p>Description: 减免订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/8/6 2:37 PM
 */
@RestController
@RequestMapping("/func/orders/relief")
public class FuncReliefOrderController {

    @Autowired
    private FuncReliefOrderServiceImpl reliefOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        ReliefOrderListReqDTO req = JsonUtils.toObj(request.getData(), ReliefOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new ReliefOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(reliefOrderService.list(req));
    }


    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/detail")
    public Object detail(@Valid ApiRequest request) throws BindException {
        OrderDetailReqDTO req = JsonUtils.toObj(request.getData(), OrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        return FuncResponseUtils.success(reliefOrderService.detail(req));
    }


}
