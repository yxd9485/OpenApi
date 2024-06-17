package com.fenbeitong.openapi.plugin.func.budget.controller;

import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckRespDTO;
import com.fenbeitong.openapi.plugin.func.budget.service.IFuncThirdBudgetService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>Title: FuncThirdBudgetController</p>
 * <p>Description: 三方预算</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 4:50 PM
 */
@RequestMapping("/func/company/budget")
@RestController
public class FuncThirdBudgetController {

    @Autowired
    private IFuncThirdBudgetService thirdBudgetService;

    @RequestMapping("/check")
    public Object checkBudget(@Valid @RequestBody ThirdBudgetCheckReqDTO checkReq) {
        ThirdBudgetCheckRespDTO checkResp = thirdBudgetService.checkBudget(checkReq);
        return FuncResponseUtils.success(checkResp);
    }
}
