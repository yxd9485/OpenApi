package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.order.dto.BusOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.BusOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FunBusOrderDetailServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 汽车票详情和列表接口
 * @author zhangpeng
 * @date 2022/3/30 3:37 下午
 */
@RestController
@RequestMapping("/func/orders/bus")
public class FuncBusOrderController {

    @Autowired
    private CommonAuthService signService;

    @Autowired
    private FunBusOrderDetailServiceImpl busOrderDetailService;

    @SuppressWarnings("all")
    @ApiOperation(value = "汽车订单列表", notes = "汽车订单列表", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/orderList")
    public Object listOrder( @Valid ApiRequest request) {
        String data = request.getData();
        BusOrderListReqDTO req = JsonUtils.toObj(data, BusOrderListReqDTO.class);
        checkReq(req);
        String appId = signService.getAppId(request);
        req.setCompanyId(appId);
        Object resp = busOrderDetailService.getBusOrderList(req);
        return FuncResponseUtils.success(resp);
    }

    @SuppressWarnings("all")
    @ApiOperation(value = "汽车订单详情", notes = "汽车订单详情", httpMethod = "POST", response = FuncResultEntity.class)
    @FuncAuthAnnotation
    @RequestMapping("/orderDetail")
    public Object detailOrder(@Valid ApiRequest request) {
        String data = request.getData();
        BusOrderDetailReqDTO req = JsonUtils.toObj(data, BusOrderDetailReqDTO.class);
        checkReq(req);
        String appId = signService.getAppId(request);
        req.setCompanyId(appId);
        Object resp = busOrderDetailService.getBusOrderDetail(req);
        return FuncResponseUtils.success(resp);
    }

    private void checkReq(Object req) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

}
