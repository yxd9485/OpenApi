package com.fenbeitong.openapi.plugin.ecology.v8.standard.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.FanWeiResult;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei.EcologyWorkFlowCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 向泛微推送工作流
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@RestController
@RequestMapping("/ecology/standard/fanwei/pushData")
public class EcologyPushWorkFlowToFanWeiController {

    @Autowired
    private EcologyWorkFlowCommonService commonApplyService;

    @RequestMapping("/tripApply")
    @ResponseBody
    public Object tripApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FanWeiResult b = commonApplyService.pushCommonReverseApply(requestBody, CommonServiceTypeConstant.TRIP);
        if (b.isSuccess()) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建差旅审批失败:" + b.getMessage());
        }
    }

    @RequestMapping("/carApply")
    @ResponseBody
    public Object carApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FanWeiResult b = commonApplyService.pushCommonReverseApply(requestBody, CommonServiceTypeConstant.CAR);
        if (b.isSuccess()) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建用车审批失败:" + b.getMessage());
        }
    }

    @RequestMapping("/dinnerApply")
    @ResponseBody
    public Object dinnerApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FanWeiResult b = commonApplyService.pushCommonReverseApply(requestBody, CommonServiceTypeConstant.DINNER);
        if (b.isSuccess()) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建用餐审批失败: " + b.getMessage());
        }
    }

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object orderApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FanWeiResult b = commonApplyService.pushCommonReverseApply(requestBody, CommonServiceTypeConstant.ORDER);
        if (b.isSuccess()) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, " 创建超规审批失败: " + b.getMessage());
        }
    }

    @RequestMapping("/reimburseApply")
    @ResponseBody
    public Object reimburseApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        FanWeiResult b = commonApplyService.pushCommonReverseApply(requestBody, CommonServiceTypeConstant.REIMBURSE);
        if (b.isSuccess()) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, " 创建自定义报销单失败: " + b.getMessage());
        }
    }
}
