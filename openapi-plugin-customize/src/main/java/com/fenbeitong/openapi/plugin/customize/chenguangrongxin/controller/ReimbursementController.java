package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.ReimbursementDetailDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.IReimbursementService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 辰光融信报销单推送简道云
 *
 * @author machao
 * @date 2022/9/16
 */
@Slf4j
@RestController
@RequestMapping("/customize/reimbursement")
public class ReimbursementController {

    @Autowired
    private IReimbursementService reimbursementService;

    @RequestMapping("/pushData")
    public Object pushData(@RequestBody ReimbursementDetailDTO data) {
        try {
            reimbursementService.pushData(data);
            return OpenapiResponseUtils.success(Maps.newHashMap());
        } catch (Exception e) {
            log.error("辰光融信报销单推送简道云异常", e);
            throw new OpenApiArgumentException("辰光融信报销单推送简道云异常");
        }
    }
}
