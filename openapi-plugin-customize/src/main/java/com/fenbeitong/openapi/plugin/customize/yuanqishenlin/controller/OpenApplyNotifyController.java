package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.controller;

import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.ApplyNotifyService;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @ClassName OpenApplyNotifyController
 * @Description 审批同意或拒绝
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/13 上午11:49
 **/
@RestController
@RequestMapping("/func/apply/order/notify")
public class OpenApplyNotifyController {
    @Autowired
    private ApplyNotifyService applyNotifyService;

    /*
     * @Description 审批通过
     **/
    @RequestMapping("/agree")
    @FuncAuthAnnotation
    @ApiOperation(value = "订单审批通过", notes = "订单审批通过", httpMethod = "POST", response = FuncResultEntity.class)
    public Object orderApplyAgree(@Valid ApiRequestBase apiRequest) throws IOException {
        applyNotifyService.applyNotifyAgree(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    /*
     * @Description 审批拒绝
     **/
    @RequestMapping("/repluse")
    @FuncAuthAnnotation
    @ApiOperation(value = "订单审批拒绝", notes = "订单审批拒绝", httpMethod = "POST", response = FuncResultEntity.class)
    public Object orderApplyRepulse(@Valid ApiRequestBase apiRequest) throws IOException {
        applyNotifyService.applyNotifyRepulse(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
