package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.ServiceTypeConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.IYunzhijiaPushApplyService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply.YunzhijiaPushApplyService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/yunzhijia/pushData")
public class YunzhijiaPushApplyController {

    @Autowired
    YunzhijiaPushApplyService yunzhijiaPushApplyService;

    @Autowired
    IYunzhijiaPushApplyService pushApplyService;

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object pushOrderApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = yunzhijiaPushApplyService.pushApply(requestBody);
        if (b) {
            return YunzhijiaResponseUtils.success(b);
        } else {
            return YunzhijiaResponseUtils.error(-1, "创建云之家订单审批失败");
        }
    }

    @RequestMapping("/tripApply")
    @ResponseBody
    public Object pushTripApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = pushApplyService.pushTripApply(requestBody);
        if (b) {
            return YunzhijiaResponseUtils.success(b);
        } else {
            return YunzhijiaResponseUtils.error(-1, "创建云之家差旅审批订单失败");
        }
    }

    @RequestMapping("/carApply")
    @ResponseBody
    public Object pushCarApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = pushApplyService.pushCarApply(requestBody);
        if (b) {
            return YunzhijiaResponseUtils.success(b);
        } else {
            return YunzhijiaResponseUtils.error(-1, "创建云之家用车审批订单失败");
        }
    }

    @RequestMapping("/dinnerApply")
    @ResponseBody
    public Object pushDinnerApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = pushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.DINNER);
        if (b) {
            return YunzhijiaResponseUtils.success(b);
        } else {
            return YunzhijiaResponseUtils.error(-1, "创建云之家用餐审批订单失败");
        }
    }

    @RequestMapping("/mallApply")
    @ResponseBody
    public Object pushMallApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = pushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.MALL);
        if (b) {
            return YunzhijiaResponseUtils.success(b);
        } else {
            return YunzhijiaResponseUtils.error(-1, "创建云之家采购审批订单失败");
        }
    }

    @RequestMapping("/takeOutApply")
    @ResponseBody
    public Object pushTakeOutApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = pushApplyService.pushCommonApply(requestBody, ServiceTypeConstant.TAKE_OUT);
        if (b) {
            return YunzhijiaResponseUtils.success(b);
        } else {
            return YunzhijiaResponseUtils.error(-1, "创建云之家外卖审批订单失败");
        }
    }

}
