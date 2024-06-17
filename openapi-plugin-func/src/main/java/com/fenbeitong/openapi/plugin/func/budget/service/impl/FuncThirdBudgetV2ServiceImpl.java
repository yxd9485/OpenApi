package com.fenbeitong.openapi.plugin.func.budget.service.impl;

import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingCreateReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingCreateRespDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetingObjectReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.service.IFuncThirdBudgetV2Service;
import com.fenbeitong.openapi.plugin.support.budget.service.AbstractBudgetV2Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@ServiceAspect
@Slf4j
public class FuncThirdBudgetV2ServiceImpl extends AbstractBudgetV2Service implements IFuncThirdBudgetV2Service {

    @Override
    public ThirdBudgetingCreateRespDTO createBudgeting(String companyId, ThirdBudgetingCreateReqDTO thirdBudgetingReqDTO) {
        String planId = super.createBudget(companyId, thirdBudgetingReqDTO);
        ThirdBudgetingCreateRespDTO thirdBudgetingCreateRespDTO = ThirdBudgetingCreateRespDTO.builder().planId(planId).build();
        return thirdBudgetingCreateRespDTO;
    }

    @Override
    public void addBudgetObjects(String companyId, ThirdBudgetingObjectReqDTO thirdBudgetingObjectReqDTO) {
        super.addObjects(companyId, thirdBudgetingObjectReqDTO.getPlanId(), thirdBudgetingObjectReqDTO.getBudgetingObject());
    }

}
