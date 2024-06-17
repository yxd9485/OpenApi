package com.fenbeitong.openapi.plugin.func.budget.service;

import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckRespDTO;

/**
 * <p>Title: IFuncThirdBudgetService</p>
 * <p>Description: 三方预算</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 5:06 PM
 */
public interface IFuncThirdBudgetService {

    /**
     * 检查第三方预算
     * @param checkReq 订单相关信息
     * @return 检查结果
     */
    ThirdBudgetCheckRespDTO checkBudget(ThirdBudgetCheckReqDTO checkReq);
}
