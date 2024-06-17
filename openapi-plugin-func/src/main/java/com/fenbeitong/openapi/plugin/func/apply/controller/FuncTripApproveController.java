package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.func.apply.service.FuncTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>Title: FuncTripApproveController</p>
 * <p>Description: 行程审批-旧版 暂不删除</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/7 10:29 AM
 */
@RestController
@RequestMapping("/func/approve")
@Api(value = "行程审批", tags = "行程审批", description = "第三方行程审批")
public class FuncTripApproveController {

    @Autowired
    private FuncTripApplyServiceImpl tripApplyService;

    @RequestMapping("/create")
    @ApiOperation(value = "创建审批单", notes = "创建行程审批单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object createTripApply(@Valid ApiRequest apiRequest) throws Exception {
        Object result = tripApplyService.createTripApply(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/change")
    @ApiOperation(value = "变更审批单", notes = "变更行程审批单", httpMethod = "POST", response = FuncResultEntity.class)
    public Object changeTripApply(@Valid ApiRequest apiRequest) throws Exception {
        Object result = tripApplyService.changeTripApply(apiRequest);
        return FuncResponseUtils.success(result);
    }

}