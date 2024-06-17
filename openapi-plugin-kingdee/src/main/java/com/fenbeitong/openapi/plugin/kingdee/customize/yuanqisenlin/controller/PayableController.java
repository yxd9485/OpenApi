package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto.KingdeePayableDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service.PayableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 账单生成应付单接口
 *
 * @author ctl
 * @date 2021/7/2
 */
@RestController
@Slf4j
@RequestMapping("/customize/yuanqisenlin")
public class PayableController {

    @Autowired
    private PayableService payableService;

    /**
     * 账单生产应付单
     *
     * @param companyId
     * @param billNo
     * @return
     */
    @GetMapping("/convertPayable")
    public Object convertPayable(String companyId, String billNo, String kingDeeCompanyFieldName, String kingDeeDeptFieldName) {
        payableService.convertPayable(companyId, billNo, kingDeeCompanyFieldName, kingDeeDeptFieldName);
        return OpenapiResponseUtils.success(new HashMap<>());
    }

    /**
     * 推送应付单
     *
     * @param data
     * @param companyId
     * @return
     */
    @PostMapping("/pushPayable/{companyId}")
    public Object pushPayable(@RequestBody KingdeePayableDTO data, @PathVariable String companyId) {
        return payableService.pushPayable(data, companyId);
    }


}
