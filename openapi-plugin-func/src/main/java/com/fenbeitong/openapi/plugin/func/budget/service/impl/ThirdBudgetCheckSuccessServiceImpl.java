package com.fenbeitong.openapi.plugin.func.budget.service.impl;

import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckRespDTO;
import com.fenbeitong.openapi.plugin.func.budget.service.IThirdBudgetCheckService;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: ThirdBudgetCheckSuccessServiceImpl</p>
 * <p>Description: 默认预算检查成功服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 6:07 PM
 */
@ServiceAspect
@Service
public class ThirdBudgetCheckSuccessServiceImpl implements IThirdBudgetCheckService {

    @Override
    public ThirdBudgetCheckRespDTO checkThirdBudget(ThirdBudgetCheckReqDTO checkReq, String result) {
        return ThirdBudgetCheckRespDTO.builder().withinBudget(1).budgetInfo("预算合规").build();
    }
}
