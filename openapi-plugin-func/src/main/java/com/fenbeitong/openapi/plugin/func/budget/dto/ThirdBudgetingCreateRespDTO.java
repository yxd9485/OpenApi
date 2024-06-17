package com.fenbeitong.openapi.plugin.func.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.core.util.EnumValidator;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingCycle;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingObjectType;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 预算编制信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdBudgetingCreateRespDTO {

    @JsonProperty("plan_id")
    private String planId;

}
