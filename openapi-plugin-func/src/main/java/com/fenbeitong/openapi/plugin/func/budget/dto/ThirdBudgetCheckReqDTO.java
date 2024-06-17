package com.fenbeitong.openapi.plugin.func.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: ThirdBudgetCheckReqDTO</p>
 * <p>Description: 三方预算检查请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 4:57 PM
 */
@Data
public class ThirdBudgetCheckReqDTO {

    /**
     * 公司id
     */
    @NotBlank(message = "公司id[company_id]不可为空")
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 部门id
     */
    @JsonProperty("org_unit_id")
    private String orgUnitId;

    /**
     * 人员id
     */
    @NotBlank(message = "人员id[employee_id]不可为空")
    @JsonProperty("employee_id")
    private String employeeId;

    /**
     * 订单id
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 订单类型
     */
    @NotNull(message = "订单类型[type]不可为空")
    private Integer type;

    /**
     * 订单金额
     */
    @NotNull(message = "订单金额[total_price]不可为空")
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * 费用归属列表
     */
    @JsonProperty("cost_attribution_list")
    private List<ThirdBudgetCheckCostAttribution> costAttributionList;

    @Data
    public static class ThirdBudgetCheckCostAttribution {

        /**
         * 费用归属id
         */
        @JsonProperty("cost_attribution_id")
        private String costAttributionId;

        /**
         * 费用归属name
         */
        @JsonProperty("cost_attribution_name")
        private String costAttributionName;

        /**
         * 费用归属类型 1.部门 2.项目
         */
        @JsonProperty("cost_attribution_category")
        private Integer costAttributionCategory;
    }
}
