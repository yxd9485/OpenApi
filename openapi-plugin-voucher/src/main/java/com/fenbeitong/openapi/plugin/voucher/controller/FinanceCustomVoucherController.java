package com.fenbeitong.openapi.plugin.voucher.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.voucher.dto.CustomizeVoucherDTO;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceCustomVoucherCreateReqDto;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceCustomVoucherService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>Title: FinanceCustomVoucherController</p>
 * <p>Description: 用户定制 账单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/24 10:25 AM
 */
@RestController
@RequestMapping("/internal/custom/voucher")
public class FinanceCustomVoucherController {

    @Autowired
    private IFinanceCustomVoucherService financeCustomVoucherService;

    @PostMapping("/createVoucherByPublicBill")
    public Object createVoucherByPublicBill(@RequestBody FinanceCustomVoucherCreateReqDto reqDto) {
        String voucherId = RandomUtils.bsonId();
        financeCustomVoucherService.createVoucherByPublicBill(voucherId, reqDto);
        Map<String, Object> res = Maps.newHashMap();
        res.put("voucher_id", voucherId);
        return OpenapiResponseUtils.success(res);
    }

    @GetMapping("/detail")
    public Object getVoucherDetail(@RequestParam("voucher_id") String voucherId) {
        CustomizeVoucherDTO customizeVoucherDTO = financeCustomVoucherService.getVoucherDetail(voucherId);
        return OpenapiResponseUtils.success(customizeVoucherDTO);
    }
}
