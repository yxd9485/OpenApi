package com.fenbeitong.openapi.plugin.customize.custom.apply.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.reimbursement.dto.ReimburseFormDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.reimbursement.service.ReimbursementService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义表单处理
 * @Auther zhang.peng
 * @Date 2022/1/20
 */
@RestController
@Slf4j
@RequestMapping("/custom/form")
public class CustomFormController {

    @Autowired
    private ReimbursementService reimbursementService;

    @RequestMapping("/init/{companyId}/{formId}")
    public Object getReimbursementInfo(@PathVariable("companyId") String companyId , @PathVariable("formId") String formId){
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(formId)){
            return OpenapiResponseUtils.error(RespCode.ARGUMENT_ERROR,"companyId 和 formId 不能为空");
        }
        ReimburseFormDetailReqDTO formDetailReqDTO = new ReimburseFormDetailReqDTO();
        formDetailReqDTO.setCompanyId(companyId);
        formDetailReqDTO.setFormId(formId);
        reimbursementService.initFormDetailByFormId(formDetailReqDTO);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

}
