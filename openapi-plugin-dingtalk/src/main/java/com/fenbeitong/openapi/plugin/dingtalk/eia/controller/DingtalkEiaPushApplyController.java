package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse.DingtalkIntranetPushApplyService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyCarDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyMultiTripDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * @Description 订单审批反向同步
 * @Author duhui
 * @Date 2020-11-05
 **/
@Controller
@RequestMapping("/dingtalk/pushData")
public class DingtalkEiaPushApplyController {
    @Autowired
    DingtalkIntranetPushApplyService dingtalkIntranetPushApplyService;

    @RequestMapping("/orderApply")
    @ResponseBody
    public Object pushOrderApply(@RequestBody String request) throws ParseException {
        boolean success = dingtalkIntranetPushApplyService.pushApply(request, OpenType.DINGTALK_EIA.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉订单审批失败");
        }
    }

    @RequestMapping("/tripApply")
    @ResponseBody
    public Object pushTripApply(HttpServletRequest request) throws ParseException {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = dingtalkIntranetPushApplyService.pushTripApply(requestBody, OpenType.DINGTALK_EIA.getType());
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉差旅审批失败");
        }
    }

    @RequestMapping("/taxiApply")
    @ResponseBody
    public Object pushTaxiApply(@RequestBody IntranetApplyCarDTO intranetApplyCarDTO) {
        boolean b = dingtalkIntranetPushApplyService.pushApply(intranetApplyCarDTO, OpenType.DINGTALK_EIA.getType(), ProcessTypeConstant.CAR_REVERSE);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉用车审批失败");
        }
    }

    @RequestMapping("/mallApply")
    @ResponseBody
    public Object pushMallApply(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = dingtalkIntranetPushApplyService.pushMallApply(requestBody, OpenType.DINGTALK_EIA.getType());
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建钉钉采购审批失败");
        }
    }

    @RequestMapping("/orderApplyRefund")
    @ResponseBody
    public Object pushOrderApplyRefund(@RequestBody String request) throws ParseException {
        boolean success = dingtalkIntranetPushApplyService.pushApplyRefund(request, OpenType.DINGTALK_EIA.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉订单退订审批失败");
        }
    }

    @RequestMapping("/orderApplyChange")
    @ResponseBody
    public Object orderApplyChange(@RequestBody String request) throws ParseException {
        boolean success = dingtalkIntranetPushApplyService.pushApplyChange(request, OpenType.DINGTALK_EIA.getType());
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉订单改签审批失败");
        }
    }

    @RequestMapping("/dinnerApply")
    @ResponseBody
    public Object pushDinnerApply(@RequestBody String request)  {
        boolean b = dingtalkIntranetPushApplyService.pushDinnerApply(request, OpenType.DINGTALK_EIA.getType());
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "创建EIA钉钉用餐审批失败");
        }
    }
    @RequestMapping("/multiTripApply/{companyId}")
    @ResponseBody
    public Object multiTripApply(@RequestBody IntranetApplyMultiTripDetailDTO multiTripDetailDTO,@PathVariable("companyId") String companyId) {
        multiTripDetailDTO.setCompanyId(companyId);
        boolean success = dingtalkIntranetPushApplyService.pushApply(multiTripDetailDTO, OpenType.DINGTALK_EIA.getType(),ProcessTypeConstant.MULTI_TRIP_REVERSE);
        if (success) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(-1, "创建企业钉钉审批失败非行程审批失败");
        }
    }
}
