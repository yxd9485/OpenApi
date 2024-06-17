package com.fenbeitong.openapi.plugin.func.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.budget.dto.ThirdBudgetingObjectDTO;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingObjectType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdBudgetingObjectReqDTO {

    @NotBlank(message = "预算方案id[plan_id]不可为空")
    @JsonProperty("plan_id")
    private String planId;

    @Valid
    @JsonProperty("budgeting_object")
    @NotNull(message = "预算对象[budgeting_object]不可为空")
    private List<ThirdBudgetingObjectDTO> budgetingObject;


}
