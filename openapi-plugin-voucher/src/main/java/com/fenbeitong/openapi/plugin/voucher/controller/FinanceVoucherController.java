package com.fenbeitong.openapi.plugin.voucher.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCreateVoucherReqDto;
import com.fenbeitong.openapi.plugin.voucher.service.IVoucherCreateService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>Title: FinanceCreateVoucherController</p>
 * <p>Description: 凭证生成 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/29 5:42 PM
 */
@RestController
@RequestMapping("/voucher")
public class FinanceVoucherController {

    @Autowired
    private IVoucherCreateService voucherCreateService;

    @RequestMapping("/create")
    public Object createVoucher(@RequestBody FinanceCreateVoucherReqDto req) {
        voucherCreateService.createVoucher(req.getCompanyId(), req.getOperatorId(), req.getOperator(), req.getVoucherType(), req.getBatchId(), req.getCallBackUrl());
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/createBySrc")
    public Object createVoucherBySrc(@RequestBody FinanceCreateVoucherReqDto req) {
        voucherCreateService.createVoucherBySrc(req.getCompanyId(), req.getOperatorId(), req.getOperator(), req.getVoucherType(), req.getBatchId(), req.getSrcList(), req.getCallBackUrl());
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/exportExcel")
    public Object exportExcel(String batchId, Long excelConfigId) {
        String url = voucherCreateService.exportExcel(batchId, excelConfigId);
        Map<String, Object> result = Maps.newHashMap();
        result.put("url", url);
        return OpenapiResponseUtils.success(result);
    }
}
