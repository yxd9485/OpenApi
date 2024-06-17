package com.fenbeitong.openapi.plugin.func.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenCostAttributionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 对公付款申请单创建实体
 *
 * @author ctl
 * @date 2022/4/22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FuncPaymentApplyCreateDTO implements Serializable {

    /**
     * 三方申请单id 必填
     */
    @JsonProperty("third_apply_id")
    @NotBlank(message = "[third_apply_id]不能为空")
    private String thirdApplyId;
    /**
     * 三方申请人id 必填
     */
    @JsonProperty("third_employee_id")
    @NotBlank(message = "[third_employee_id]不能为空")
    private String thirdEmployeeId;
    /**
     * 付款金额 单位 元 必填
     */
    @NotNull(message = "[estimatedTotalAmount]不能为空")
    @JsonProperty("estimated_total_amount")
    private BigDecimal estimatedTotalAmount;
    /**
     * 申请单名称 必填
     */
    @JsonProperty("payment_name")
    @NotBlank(message = "[payment_name]不能为空")
    private String paymentName;
    /**
     * 分贝通供应商ID 必填 用户
     * 通过查询接口查到
     */
    @JsonProperty("supplier_id")
    @NotNull(message = "[supplier_id]不能为空")
    private Integer supplierId;
    /**
     * 分贝通合同id 非必填
     */
    @JsonProperty("contract_id")
    private String contractId;
    /**
     * 分贝通付款计划id 非必填
     */
    @JsonProperty("payment_plan_id")
    private String paymentPlanId;
    /**
     * 默认为2（无发票）
     * 1：已开发票
     * 0：待开发票
     * 2：无发票
     *
     * @see com.fenbeitong.openapi.plugin.support.payment.common.InvoiceTypeEnum
     */
    @JsonProperty("invoice_type")
    private Integer invoiceType;
    /**
     * 分贝通发票id 非必填
     */
    @JsonProperty("invoice_ids")
    private List<String> invoiceIds;
    /**
     * 分贝通付款主体id 必填
     */
    @JsonProperty("payment_account_id")
    @NotBlank(message = "[payment_account_id]不能为空")
    private String paymentAccountId;
    /**
     * 付款时间 yyyy-MM-dd 必填
     */
    @JsonProperty("payment_time")
    @NotBlank(message = "[payment_time]不能为空")
    private String paymentTime;
    /**
     * 付款用途 必填
     */
    @JsonProperty("payment_use")
    @NotBlank(message = "[payment_use]不能为空")
    private String paymentUse;
    /**
     * 申请事由 200字符 必填
     */
    @JsonProperty("apply_reason")
    @NotBlank(message = "[apply_reason]不能为空")
    @Length(max = 200, message = "[apply_reason]不能超过200字符")
    private String applyReason;
    /**
     * 申请事由补充说明 500字符 非必填
     */
    @JsonProperty("apply_remark")
    @Length(max = 500, message = "[apply_remark]不能超过500字符")
    private String applyRemark;
    /**
     * 三方备注 非必填
     */
    @JsonProperty("third_remark")
    private String thirdRemark;
    /**
     * 费用类别 非必填
     */
    @JsonProperty("cost_category")
    private CostCategory costCategory;
    /**
     * 费用归属 非必填
     */
    @JsonProperty("cost_attributions")
    private List<OpenCostAttributionDTO> costAttributions;

    /**
     * 费用类别
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class CostCategory implements Serializable {
        private String code;
        private String name;
    }

}
