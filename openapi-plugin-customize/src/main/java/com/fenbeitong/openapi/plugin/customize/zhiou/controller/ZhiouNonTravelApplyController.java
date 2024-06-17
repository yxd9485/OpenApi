package com.fenbeitong.openapi.plugin.customize.zhiou.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.ZhiouNonTravelApplyService;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName LandrayAndBeisenPushApplyController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/30
 **/
@RestController
@RequestMapping("/customize/zhiou/apply")
public class ZhiouNonTravelApplyController {

    @Autowired
    ZhiouNonTravelApplyService zhiouNonTravelApplyService;

    @RequestMapping("/nonTravelApplyPush")
    @ApiOperation(value = "推送非行程审批单", notes = "推送非行程审批单", httpMethod = "POST")
    public Object nonTravelApplyPush(HttpServletRequest request, @RequestParam(value = "companyId") String companyId) {
        if (zhiouNonTravelApplyService.nonTravelApplyPush(request, companyId)) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } else {
            return OpenapiResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
        }
    }
}
