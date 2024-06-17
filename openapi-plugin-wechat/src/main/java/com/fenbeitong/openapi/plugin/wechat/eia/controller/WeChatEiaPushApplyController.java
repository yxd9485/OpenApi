package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.service.apply.WeChatEiaPushApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@Controller
@RequestMapping("/wechat/pushData")
public class WeChatEiaPushApplyController {
    @Autowired
    WeChatEiaPushApplyService weChatEiaPushApplyService;

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object pushOrderApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = weChatEiaPushApplyService.pushApply(requestBody);
        if (b) {
            return WechatResponseUtils.success(b);
        } else {
            return WechatResponseUtils.error(-1, "创建企业微信订单审批失败");
        }
    }

    @RequestMapping("/tripApply")
    @ResponseBody
    public Object pushTripApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = weChatEiaPushApplyService.pushTripApply(requestBody);
        if (b) {
            return WechatResponseUtils.success(b);
        } else {
            return WechatResponseUtils.error(-1, "创建企业微信差旅审批失败");
        }
    }


    @RequestMapping("/taxiApply")
    @ResponseBody
    public Object pushTaxiApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = weChatEiaPushApplyService.pushTaxiApply(requestBody);
        if (b) {
            return WechatResponseUtils.success(b);
        } else {
            return WechatResponseUtils.error(-1, "创建企业微信用车审批失败");
        }
    }

    @RequestMapping("/orderApplyChange")
    @ResponseBody
    public Object pushOrderChangeApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = weChatEiaPushApplyService.pushOrderChangeApply(requestBody);
        if (b) {
            return WechatResponseUtils.success(b);
        } else {
            return WechatResponseUtils.error(-1, "创建企业微信用车审批失败");
        }
    }


    @RequestMapping("/orderApplyRefund")
    @ResponseBody
    public Object pushOrderRefundApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = weChatEiaPushApplyService.pushOrderRefundApply(requestBody);
        if (b) {
            return WechatResponseUtils.success(b);
        } else {
            return WechatResponseUtils.error(-1, "创建企业微信用车审批失败");
        }
    }

    @RequestMapping("/mallApply")
    @ResponseBody
    public Object pushMallApply(HttpServletRequest request){
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = weChatEiaPushApplyService.pushMallApply(requestBody);
        if (b) {
            return WechatResponseUtils.success(b);
        } else {
            return WechatResponseUtils.error(-1, "创建企业微信采购审批失败");
        }
    }
}
