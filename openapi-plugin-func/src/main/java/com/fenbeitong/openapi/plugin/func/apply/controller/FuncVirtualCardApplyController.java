package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.dto.VirtualCardApplyReqDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.VirtualCardRefundReqDTO;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncVirtualCardApplyService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 虚拟卡审批
 * Created by log.chang on 2020/4/27.
 */
@Controller
@RequestMapping("/func/apply/virtualCard")
@Slf4j
public class FuncVirtualCardApplyController {

    @Autowired
    private FuncVirtualCardApplyService funcVirtualCardApplyService;
    @Autowired
    private CommonAuthService commonAuthService;

    @RequestMapping("/create")
    //@ApiOperation(value = "创建审批单", notes = "创建外卖审批单", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object createVirtualCardApply(@Valid ApiRequest apiRequest) throws BindException {
        Map<String, String> stringStringMap = commonAuthService.signCheck(apiRequest);
        @NotBlank(message = "数据[data]不可为空") String data = apiRequest.getData();
        VirtualCardApplyReqDTO virtualCardApplyReqDTO = JsonUtils.toObj(data, VirtualCardApplyReqDTO.class);
        Object fbTakeawayApprove = funcVirtualCardApplyService.createVirtualCardApply(stringStringMap.get("company_id"), stringStringMap.get("employee_id"), stringStringMap.get("employee_type"), virtualCardApplyReqDTO);
        return FuncResponseUtils.success(fbTakeawayApprove);
    }

    @RequestMapping("/refund")
    @ResponseBody
    @FuncAuthAnnotation
    public Object refundVirtualCardCredit(@Valid ApiRequestBase apiRequest) throws BindException, InvocationTargetException, IllegalAccessException {
        @NotBlank(message = "数据[data]不可为空") String data = apiRequest.getData();
        VirtualCardRefundReqDTO virtualCardRefundReqDTO = JsonUtils.toObj(data, VirtualCardRefundReqDTO.class);
        String appId = commonAuthService.getAppId(apiRequest);
        Object refundRes = funcVirtualCardApplyService.refundVirtualCardCredit(appId,virtualCardRefundReqDTO);
        return FuncResponseUtils.success(refundRes);
    }
}
