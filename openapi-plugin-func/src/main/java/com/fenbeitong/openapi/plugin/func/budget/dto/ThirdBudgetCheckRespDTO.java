package com.fenbeitong.openapi.plugin.func.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ThirdBudgetCheckRespDTO</p>
 * <p>Description: 三方预算检查结果</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 5:03 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThirdBudgetCheckRespDTO {

    /**
     * 1:未超出预算允许下单;2:超出预算禁止下单
     */
    @JsonProperty("within_budget")
    private Integer withinBudget;

    /**
     * 预算信息
     */
    @JsonProperty("budget_info")
    private String budgetInfo;
}
