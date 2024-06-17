package com.fenbeitong.openapi.plugin.func.budget.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingCreateReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingCreateRespDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingObjectReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.service.IFuncThirdBudgetV2Service;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 三方预算V2
 */
@RequestMapping("/func/budgetV2")
@RestController
public class FuncThirdBudgetV2Controller {

    @Autowired
    private IFuncThirdBudgetV2Service thirdBudgetV2Service;

    /**
     * 创建预算编制
     *
     * @param httpRequest
     * @param request
     * @return
     * @throws BindException
     */
    @FuncAuthAnnotation
    @RequestMapping("/createBudget")
    public Object createBudget(HttpServletRequest httpRequest, ApiRequestNoEmployee request) throws BindException {
        ThirdBudgetingCreateReqDTO thirdBudgetingReqDTO = JsonUtils.toObj(request.getData(), ThirdBudgetingCreateReqDTO.class);
        ValidatorUtils.validateBySpring(thirdBudgetingReqDTO);
        String companyId = (String) httpRequest.getAttribute("companyId");
        ThirdBudgetingCreateRespDTO budgeting = thirdBudgetV2Service.createBudgeting(companyId, thirdBudgetingReqDTO);
        return FuncResponseUtils.success(budgeting);
    }

    @FuncAuthAnnotation
    @RequestMapping("/addBudgetObjects")
    public Object addBudgetObjects(HttpServletRequest httpRequest, ApiRequestNoEmployee request) throws BindException {
        ThirdBudgetingObjectReqDTO thirdBudgetingObjectReqDTO = JsonUtils.toObj(request.getData(), ThirdBudgetingObjectReqDTO.class);
        ValidatorUtils.validateBySpring(thirdBudgetingObjectReqDTO);
        String companyId = (String) httpRequest.getAttribute("companyId");
        thirdBudgetV2Service.addBudgetObjects(companyId, thirdBudgetingObjectReqDTO);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
