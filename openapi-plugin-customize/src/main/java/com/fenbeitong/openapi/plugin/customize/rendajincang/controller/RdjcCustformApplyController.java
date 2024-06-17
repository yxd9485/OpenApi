package com.fenbeitong.openapi.plugin.customize.rendajincang.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.rendajincang.service.IRdjcCustformApplyService;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName RdjcCustformApplyController
 * @Description 自定义申请单推送人大金仓
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/10/12
 **/
@RequestMapping("/customize/rdjcCustformApply")
@RestController
public class RdjcCustformApplyController {
    @Autowired
    IRdjcCustformApplyService rdjcCustformApplyService;

    @RequestMapping("/custformApplyPush")
    @ApiOperation(value = "过期自定义申请单推送", notes = "过期自定义申请单推送")
    public Object custformApplyPush(HttpServletRequest request, @RequestParam(value = "companyId") String companyId) {
        rdjcCustformApplyService.pushExpiredData(request, companyId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
