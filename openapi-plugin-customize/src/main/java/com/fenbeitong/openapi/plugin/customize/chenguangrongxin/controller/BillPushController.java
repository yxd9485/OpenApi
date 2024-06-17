package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.impl.BillServiceImpl;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName BillPushController
 * @Description 推送辰光融信账单数据
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/19 下午9:13
 **/
@RestController
@RequestMapping("/customize/bill")
public class BillPushController {

    @Autowired
    private BillServiceImpl billService;

    @RequestMapping("/pushData")
    public Object pushData(@RequestParam(value = "companyId") String companyId,@RequestParam(value = "billNo") String billNo) {
        billService.pushBillData(companyId,billNo);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
