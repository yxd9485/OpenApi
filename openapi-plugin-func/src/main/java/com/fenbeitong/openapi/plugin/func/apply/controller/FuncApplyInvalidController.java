package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyInvalidReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.ApplyInvalidService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncApplyInvalidController</p>
 * <p>Description: 审批单作废</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/7 10:29 AM
 */
@RestController
@RequestMapping("/func/apply/invalid")
public class FuncApplyInvalidController {

    @Autowired
    private ApplyInvalidService applyInvalidService;

    @RequestMapping("/updateApplyInvalid")
    @FuncAuthAnnotation
    public Object updateApplyInvalid(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequest) {
        ApplyInvalidReqDTO req = JsonUtils.toObj(apiRequest.getData(), ApplyInvalidReqDTO.class);
        if (req != null) {
            String companyId = (String) httpRequest.getAttribute("companyId");
            req.setCompanyId(companyId);
        }
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
        applyInvalidService.updateApplyInvalid(req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

}
