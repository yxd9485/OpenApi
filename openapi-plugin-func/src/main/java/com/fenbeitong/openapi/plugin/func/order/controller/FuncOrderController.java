package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.order.service.FuncOrderService;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 功能集成-订单控制器
 * Created by log.chang on 2019/12/3.
 */
@RestController
@RequestMapping("/func/orders")
@Api(value = "订单服务", tags = "订单服务", description = "订单服务")
public class FuncOrderController {

    @Autowired
    private FuncOrderService funcOrderService;

    @RequestMapping("/orderParam")
    @ApiOperation(value = "获取订单自定义参数", notes = "获取订单自定义参数", httpMethod = "POST", response = FuncResultEntity.class)
    public Object orderParam(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcOrderService.orderParam(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/orderParam/save")
    @ApiOperation(value = "保存订单自定义参数", notes = "保存订单自定义参数", httpMethod = "POST", response = FuncResultEntity.class)
    public Object insertOderParam(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcOrderService.insertOderParam(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/findAirPlaneOrder")
    @ApiOperation(value = "根据订单id查询机票订单", notes = "根据订单id查询机票订单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object findAirPlaneOrder(@Valid ApiRequestNoEmployee apiRequest, @RequestParam("order_id") String orderId) throws Exception {
        Object result = funcOrderService.findAirPlaneOrder(apiRequest, orderId);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/findCarOrder")
    @ApiOperation(value = "根据订单id查询用车订单", notes = "根据订单id查询用车订单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object findCarOrder(@Valid ApiRequestNoEmployee apiRequest, @RequestParam("order_id") String orderId) throws Exception {
        Object result = funcOrderService.findCarOrder(apiRequest, orderId);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/findTrainOrder")
    @ApiOperation(value = "根据订单id查询火车订单", notes = "根据订单id查询火车订单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object findTrainOrder(@Valid ApiRequestNoEmployee apiRequest, @RequestParam("order_id") String orderId) throws Exception {
        Object result = funcOrderService.findTrainOrder(apiRequest, orderId);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/findHotelOrder")
    @ApiOperation(value = "根据订单id查询酒店订单", notes = "根据订单id查询酒店订单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object findOrderByIdAndType(@Valid ApiRequestNoEmployee apiRequest, @RequestParam("order_id") String orderId) throws Exception {
        Object result = funcOrderService.findHotelOrder(apiRequest, orderId);
        return FuncResponseUtils.success(result);
    }
}
