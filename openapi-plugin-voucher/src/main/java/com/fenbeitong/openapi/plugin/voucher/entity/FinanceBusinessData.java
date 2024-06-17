package com.fenbeitong.openapi.plugin.voucher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by huangsiyuan on 2021/09/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "finance_business_data")
public class FinanceBusinessData {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 类型 1:报销单;2:商务消费账单;3:对公付款;4:个人消费账单
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 子类型 0:默认 1:商务消费账单服务费;
     */
    @Column(name = "sub_type")
    private Integer subType;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 年
     */
    @Column(name = "year")
    private Integer year;

    /**
     * 月
     */
    @Column(name = "month")
    private Integer month;

    /**
     * 人员Id
     */
    @Column(name = "employee_id")
    private String employeeId;

    /**
     * 人员名称
     */
    @Column(name = "employee_name")
    private String employeeName;

    /**
     * 员工类型 1:组织架构内人员 2:外部人员
     */
    @Column(name = "employee_type")
    private Integer employeeType;

    /**
     * 人员编码
     */
    @Column(name = "employee_code")
    private String employeeCode;

    /**
     * 部门id
     */
    @Column(name = "org_unit_id")
    private String orgUnitId;

    /**
     * 部门名称
     */
    @Column(name = "org_unit_name")
    private String orgUnitName;

    /**
     * 部门全路径
     */
    @Column(name = "org_unit_full_name")
    private String orgUnitFullName;

    /**
     * 项目ID
     */
    @Column(name = "cost_center_id")
    private String costCenterId;

    /**
     * 项目编号
     */
    @Column(name = "cost_center_code")
    private String costCenterCode;

    /**
     * 项目名称
     */
    @Column(name = "cost_center_name")
    private String costCenterName;

    /**
     * 供应商编码
     */
    @Column(name = "supplier_code")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @Column(name = "supplier_name")
    private String supplierName;

    /**
     * 场景类型
     */
    @Column(name = "business_type")
    private Integer businessType;

    /**
     * 场景类型名称
     */
    @Column(name = "business_name")
    private String businessName;

    /**
     * 费用类别Id
     */
    @Column(name = "cost_category_id")
    private String costCategoryId;

    /**
     * 费用类别名称
     */
    @Column(name = "cost_category")
    private String costCategory;

    /**
     * 事由
     */
    @Column(name = "reasons")
    private String reasons;

    /**
     * 批次Id
     */
    @Column(name = "batch_id")
    private String batchId;

    /**
     * 业务Id
     */
    @Column(name = "business_id")
    private String businessId;

    /**
     * 业务行Id
     */
    @Column(name = "business_line_id")
    private String businessLineId;

    /**
     * 业务日期
     */
    @Column(name = "business_date")
    private String businessDate;

    /**
     * 应收总金额/票面金额
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    /**
     * 税额
     */
    @Column(name = "tax_price")
    private BigDecimal taxPrice;

    /**
     * 票价
     */
    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    /**
     * 企业支付金额
     */
    @Column(name = "company_pay_price")
    private BigDecimal companyPayPrice;

    /**
     * 服务费金额
     */
    @Column(name = "service_fee")
    private BigDecimal serviceFee;

    /**
     * 退票金额
     */
    @Column(name = "refund_fee")
    private BigDecimal refundFee;

    /**
     * 机建费
     */
    @Column(name = "airport_fee")
    private BigDecimal airportFee;

    /**
     * 燃油费
     */
    @Column(name = "fuel_fee")
    private BigDecimal fuelFee;

    /**
     * 业务扩展json
     */
    @Column(name = "business_ext_json")
    private String businessExtJson;

    /**
     * 扩展字段1
     */
    @Column(name = "ext1")
    private String ext1;

    /**
     * 扩展字段2
     */
    @Column(name = "ext2")
    private String ext2;

    /**
     * 扩展字段3
     */
    @Column(name = "ext3")
    private String ext3;

    /**
     * 扩展字段4
     */
    @Column(name = "ext4")
    private String ext4;

    /**
     * 扩展字段5
     */
    @Column(name = "ext5")
    private String ext5;

    /**
     * 扩展字段6
     */
    @Column(name = "ext6")
    private String ext6;

    /**
     * 扩展字段7
     */
    @Column(name = "ext7")
    private String ext7;

    /**
     * 扩展字段8
     */
    @Column(name = "ext8")
    private String ext8;

    /**
     * 扩展字段9
     */
    @Column(name = "ext9")
    private String ext9;

    /**
     * 扩展字段10
     */
    @Column(name = "ext10")
    private String ext10;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;


}
