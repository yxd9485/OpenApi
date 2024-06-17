package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.controller;

import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.NonTravelApplyService;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.YqslCarApplyService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ClassName CustmTripApplyController
 * @Description 元气森林差旅审批
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/1 上午11:20
 **/
@RestController
@RequestMapping("/func/apply")
public class CustmTripApplyController {
    @Autowired
    private NonTravelApplyService nonTravelApplyService;

    @Autowired
    private YqslCarApplyService carApplyService;

    @RequestMapping("/nontravel/create")
    @ApiOperation(value = "创建非行程审批单", notes = "创建非行程审批单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createNonTravelApply(@Valid ApiRequestBase apiRequest) throws Exception {
        FuncResultEntity result = nonTravelApplyService.createTripApply(apiRequest);
        return result;
    }
    @RequestMapping("/takeaway/car/create")
    @ApiOperation(value = "创建用车审批单", notes = "创建用车审批单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createCarApply(@Valid ApiRequestBase apiRequest) throws Exception {
        Object result = carApplyService.createCarApply(apiRequest);
        return FuncResponseUtils.success(result);
    }
}
