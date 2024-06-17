package com.fenbeitong.openapi.plugin.func.budget.service;

import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingCreateReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingCreateRespDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingObjectReqDTO;

public interface IFuncThirdBudgetV2Service {

    ThirdBudgetingCreateRespDTO createBudgeting(String companyId, ThirdBudgetingCreateReqDTO ThirdBudgetingReqDTO);

    void addBudgetObjects(String companyId, ThirdBudgetingObjectReqDTO thirdBudgetingObjectReqDTO);

}
