package com.fenbeitong.openapi.plugin.func.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: ThirdBudgetCheckReqDTO</p>
 * <p>Description: 三方预算检查推送请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 4:57 PM
 */
@Data
public class ThirdBudgetCheckPushReqDTO {

    /**
     * 部门id
     */
    @JsonProperty("org_unit_id")
    private String orgUnitId;

    /**
     * 部门名称
     */
    @JsonProperty("org_unit_name")
    private String orgUnitName;

    /**
     * 部门全名
     */
    @JsonProperty("org_unit_full_name")
    private String orgUnitFullName;

    /**
     * 人员id
     */
    @JsonProperty("employee_id")
    private String employeeId;

    /**
     * 人员姓名
     */
    @JsonProperty("employee_name")
    private String employeeName;

    /**
     * 订单id
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 订单类型
     */
    private Integer type;

    /**
     * 订单金额
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * 费用归属列表
     */
    @JsonProperty("cost_attribution_list")
    private List<ThirdBudgetCheckPushCostAttribution> costAttributionList;

    @Data
    public static class ThirdBudgetCheckPushCostAttribution {

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
