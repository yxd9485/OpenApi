package com.fenbeitong.openapi.plugin.customize.dasheng.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.dasheng.dto.OpenEbsBillDetailDto;
import com.fenbeitong.openapi.plugin.customize.dasheng.service.IDashengBillService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: DashengBillController</p>
 * <p>Description: 51talk账单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/19 4:43 PM
 */
@RestController
@RequestMapping("/customize/dasheng/bill")
public class DashengBillController {

    @Autowired
    private IDashengBillService dashengBillService;

    @RequestMapping("/saveAndPushData")
    public Object saveAndPushData(String companyId, String billNo) {
        dashengBillService.saveAndPushData(companyId, billNo);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/saveBillData")
    public Object saveBillData(String companyId, String billNo) {
        dashengBillService.saveBillData(companyId, billNo);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/updateBillData")
    public Object updateBillData(@RequestBody OpenEbsBillDetailDto openEbsBillDetailDto) {
        dashengBillService.updateBillData(openEbsBillDetailDto);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/pushBillData")
    public Object pushBillData(String companyId, String billNo, int year, int month) {
        dashengBillService.pushBill(companyId, billNo, year, month);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


}
