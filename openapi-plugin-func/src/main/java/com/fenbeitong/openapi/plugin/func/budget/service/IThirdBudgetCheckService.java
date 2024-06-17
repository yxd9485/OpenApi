package com.fenbeitong.openapi.plugin.func.budget.service;

import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckRespDTO;

/**
 * <p>Title: IThirdBudgetCheckService</p>
 * <p>Description: 第三方预算检查服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 6:06 PM
 */
public interface IThirdBudgetCheckService {

    /**
     * 检查三方预算
     * @param checkReq
     * @param result
     * @return
     */
    ThirdBudgetCheckRespDTO checkThirdBudget(ThirdBudgetCheckReqDTO checkReq, String result);
}
