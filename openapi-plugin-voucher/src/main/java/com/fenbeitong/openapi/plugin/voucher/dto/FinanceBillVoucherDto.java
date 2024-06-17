package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: FinanceBillVoucherDto</p>
 * <p>Description: 账单数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/25 3:30 PM
 */
@Data
public class FinanceBillVoucherDto {

    /**
     * 摘要
     */
    private String summary;

    private Integer year;

    private Integer month;

    @JsonProperty("batch_id")
    private String batchId;

    /**
     * 主键ID
     */
    private String id;
    /**
     * 企业ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 订单ID
     */
    @JsonProperty("order_id")
    private String orderId;
    /**
     * 场景名称
     */
    @JsonProperty("business_name")
    private String businessName;
    /**
     * 人员名称
     */
    @JsonProperty("employee_name")
    private String employeeName;
    /**
     * 人员类型 1：组织架构内人员 2：外部人员
     */
    @JsonProperty("employee_type")
    private Integer employeeType;
    /**
     * 部门全路径
     */
    @JsonProperty("org_unit_full_name")
    private String orgUnitFullName;
    /**
     * 项目名称
     */
    @JsonProperty("cost_center_name")
    private String costCenterName;
    /**
     * 项目编码
     */
    @JsonProperty("cost_center_code")
    private String costCenterCode;
    /**
     * 企业支付
     */
    @JsonProperty("company_pay_price")
    private BigDecimal companyPayPrice;
    /**
     * 服务费
     */
    private BigDecimal free;
    /**
     * 应收总金额
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    /**
     * 票价
     */
    @JsonProperty("ticket_price")
    private BigDecimal ticketPrice;
    /**
     * 基建费
     */
    private BigDecimal airrax;
    /**
     * 燃油税
     */
    @JsonProperty("fuel_tax")
    private BigDecimal fuelTax;
    /**
     * 退票费
     */
    @JsonProperty("refund_fee")
    private BigDecimal refundFee;
    /**
     * 事由
     */
    private String reasons;
    /**
     * 订单日期
     */
    @JsonProperty("order_create_date")
    private String orderCreateDate;

}
