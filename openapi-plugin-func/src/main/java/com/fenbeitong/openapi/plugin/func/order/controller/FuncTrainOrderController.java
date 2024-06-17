
package com.fenbeitong.openapi.plugin.func.order.controller;


import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FuncTrainOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncTrainOrderController</p>
 * <p>Description: 火车订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/17 2:37 PM
 */
@RestController
@RequestMapping("/func/orders/train")
public class FuncTrainOrderController {

    @Autowired
    private FuncTrainOrderServiceImpl trainOrderService;

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        TrainOrderListReqDTO req = JsonUtils.toObj(request.getData(), TrainOrderListReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new TrainOrderListReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(trainOrderService.list(req));
    }

    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/detail")
    public Object detail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        TrainOrderDetailReqDTO req = JsonUtils.toObj(request.getData(), TrainOrderDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(trainOrderService.detail(req));
    }


}

