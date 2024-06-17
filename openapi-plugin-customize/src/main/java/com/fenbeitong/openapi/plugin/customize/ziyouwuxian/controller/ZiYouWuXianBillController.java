package com.fenbeitong.openapi.plugin.customize.ziyouwuxian.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.ziyouwuxian.service.IZiYouWuXianBillService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: ZiYouWuXianBillController</p>
 * <p>Description: 自由无限账单数据存储</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/10/26 4:43 PM
 */
@RestController
@RequestMapping("/customize/ziyouwuxian/bill")
public class ZiYouWuXianBillController {

    @Autowired
    private IZiYouWuXianBillService ziYouWuXianBillService;

    @RequestMapping("/pushBill")
    public Object pushBill(String companyId, String billNo) {
        ziYouWuXianBillService.pushBill(companyId, billNo);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/create/{billNo}/{companyId}/{type}")
    public Object createSumData(@PathVariable("billNo") String billNo, @PathVariable("companyId") String companyId, @PathVariable("type") String type) {
        ziYouWuXianBillService.createBill(companyId, billNo, type);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/send/{billNo}/{companyId}")
    public Object sendBillExcel(@PathVariable("billNo") String billNo, @PathVariable("companyId") String companyId) {
        ziYouWuXianBillService.sendBill(companyId, billNo);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
