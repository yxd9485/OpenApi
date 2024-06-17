package com.fenbeitong.openapi.plugin.func.reimburse.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.reimburse.dto.RemiUpdStatusDTO;
import com.fenbeitong.openapi.plugin.func.reimburse.service.FuncRemiService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;



@RestController
@RequestMapping("/func/reimb")
public class FuncRemiController {
    @Autowired
    private FuncRemiService funcRemiService;

    @Autowired
    private CommonAuthService signService;

    @FuncAuthAnnotation
    @RequestMapping("/detail")
    @ApiOperation(value = "查询报销单详情", notes = "查询报销单详情", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getListByApplyId(@Valid ApiRequestBase apiRequest) throws Exception {
        signService.checkSign(apiRequest);
        String appId = signService.getAppId(apiRequest);
        RemiDetailReqDTO req = JsonUtils.toObj(apiRequest.getData(), RemiDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        Object result =funcRemiService.getRemiDetailInfo(req,appId);
        return FuncResponseUtils.success(result);
    }

    @FuncAuthAnnotation
    @RequestMapping("/batchUpdateStatus")
    @ApiOperation(value = "批量更新报销单状态为已付款", notes = "批量更新报销单状态为已付款", httpMethod = "POST", response = FuncResultEntity.class)
    public Object getDetailByApplyId(@Valid ApiRequestBase apiRequest) throws Exception {
        RemiUpdStatusDTO req = JsonUtils.toObj(apiRequest.getData(), RemiUpdStatusDTO.class);
        Object result =funcRemiService.updateRemiStatus(req);
        if(ObjectUtils.isEmpty(result)){
            return FuncResponseUtils.success(result);
        }
        return FuncResponseUtils.error(-999,"更新报销单状态存在错误",result);
    }
}
