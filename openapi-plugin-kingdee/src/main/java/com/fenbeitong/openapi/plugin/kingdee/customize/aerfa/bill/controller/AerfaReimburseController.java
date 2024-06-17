package com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto.KingdeeSaveReimbursementDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.service.AerfaReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 阿尔法账单生成与推送
 * @author helu
 * @date 2022/9/9 上午11:17
 */
@RestController
@Slf4j
@RequestMapping("/customize/aerfa")
public class AerfaReimburseController {

    @Autowired
    private AerfaReimburseService payableService;

    /**
     * 账单计算转换并记表
     *
     * @param companyId
     * @param billNo
     * @return
     */
    @GetMapping("/convertReimburse")
    public Object converReimburse(String companyId, String billNo) {
        payableService.convertReimb(companyId, billNo);
        return OpenapiResponseUtils.success(new HashMap<>());
    }

    /**
     * 阿尔法账单推送金蝶费用报销单
     * @author helu
     * @date 2022/9/20 下午5:12
     * @param data
     * @param companyId
     * @return Object
     */
    @PostMapping("/pushReimburse")
    public Object pushReimburse(@RequestBody KingdeeSaveReimbursementDTO data, @RequestParam(value="companyId") String companyId) {
        return   payableService.pushReimb(data, companyId);
    }
}

