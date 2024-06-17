package com.fenbeitong.openapi.plugin.voucher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by huangsiyuan on 2020/12/29.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_voucher_draft")
public class OpenVoucherDraft {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 年
     */
    @Column(name = "YEAR")
    private Integer year;

    /**
     * 月
     */
    @Column(name = "MONTH")
    private Integer month;

    /**
     * 状态 -1:初始化;1:已生效
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 科目编码
     */
    @Column(name = "ACCOUNT_CODE")
    private String accountCode;

    /**
     * 科目名称
     */
    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    /**
     * 凭证分录类型 1:业务线借方;2:业务线进项税;3:服务费借方;4:服务费进项税;5:贷方科目
     */
    @Column(name = "VOUCHER_TYPE")
    private Integer voucherType;

    /**
     * 凭证分录类型名称
     */
    @Column(name = "VOUCHER_TYPE_NAME")
    private String voucherTypeName;

    /**
     * 摘要信息
     */
    @Column(name = "SUMMARY")
    private String summary;

    /**
     * 借方金额
     */
    @Column(name = "DEBIT")
    private BigDecimal debit;

    /**
     * 贷方金额
     */
    @Column(name = "CREDIT")
    private BigDecimal credit;

    /**
     * 人员编码
     */
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    /**
     * 人员名称
     */
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    /**
     * 部门编码
     */
    @Column(name = "DEPT_CODE")
    private String deptCode;

    /**
     * 部门名称
     */
    @Column(name = "DEPT_NAME")
    private String deptName;

    /**
     * 项目编码
     */
    @Column(name = "PROJECT_CODE")
    private String projectCode;

    /**
     * 项目名称
     */
    @Column(name = "PROJECT_NAME")
    private String projectName;

    /**
     * 供应商编码
     */
    @Column(name = "SUPPLIER_CODE")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @Column(name = "SUPPLIER_NAME")
    private String supplierName;

    /**
     * 制单人id
     */
    @Column(name = "OPERATOR_ID")
    private String operatorId;

    /**
     * 制单人名称
     */
    @Column(name = "OPERATOR_NAME")
    private String operatorName;

    /**
     * 制单日期
     */
    @Column(name = "VOUCHER_DATE")
    private String voucherDate;

    /**
     * 分贝通项目编码
     */
    @Column(name = "COST_CENTER_CODE")
    private String costCenterCode;

    /**
     * 分贝通项目名称
     */
    @Column(name = "COST_CENTER_NAME")
    private String costCenterName;

    /**
     * 分贝通部门路径
     */
    @Column(name = "ORG_UNIT_FULL_NAME")
    private String orgUnitFullName;

    /**
     * 项目核算
     */
    @Column(name = "PROJECT_ACCOUNTING")
    private Integer projectAccounting;

    /**
     * 部门核算
     */
    @Column(name = "DEPARTMENT_ACCOUNTING")
    private Integer departmentAccounting;

    /**
     * 人员核算
     */
    @Column(name = "EMPLOYEE_ACCOUNTING")
    private Integer employeeAccounting;

    /**
     * 供应商核算
     */
    @Column(name = "SUPPLIER_ACCOUNTING")
    private Integer supplierAccounting;

    /**
     * 批次id
     */
    @Column(name = "BATCH_ID")
    private String batchId;

    /**
     * 科目编码
     */
    @Column(name = "BATCH_LINE_ID")
    private String batchLineId;

    /**
     * 备用字段1
     */
    @Column(name = "ATTR1")
    private String attr1;

    /**
     * 备用字段2
     */
    @Column(name = "ATTR2")
    private String attr2;

    /**
     * 备用字段3
     */
    @Column(name = "ATTR3")
    private String attr3;

    /**
     * 备用字段4
     */
    @Column(name = "ATTR4")
    private String attr4;

    /**
     * 备用字段5
     */
    @Column(name = "ATTR5")
    private String attr5;

    /**
     * 备用字段6
     */
    @Column(name = "ATTR6")
    private String attr6;

    /**
     * 备用字段7
     */
    @Column(name = "ATTR7")
    private String attr7;

    /**
     * 备用字段8
     */
    @Column(name = "ATTR8")
    private String attr8;

    /**
     * 备用字段9
     */
    @Column(name = "ATTR9")
    private String attr9;

    /**
     * 备用字段10
     */
    @Column(name = "ATTR10")
    private String attr10;

    /**
     * 备用字段11
     */
    @Column(name = "ATTR11")
    private String attr11;

    /**
     * 备用字段12
     */
    @Column(name = "ATTR12")
    private String attr12;

    /**
     * 备用字段13
     */
    @Column(name = "ATTR13")
    private String attr13;

    /**
     * 备用字段14
     */
    @Column(name = "ATTR14")
    private String attr14;

    /**
     * 备用字段15
     */
    @Column(name = "ATTR15")
    private String attr15;

    /**
     * 备用字段16
     */
    @Column(name = "ATTR16")
    private String attr16;

    /**
     * 备用字段17
     */
    @Column(name = "ATTR17")
    private String attr17;

    /**
     * 备用字段18
     */
    @Column(name = "ATTR18")
    private String attr18;

    /**
     * 备用字段19
     */
    @Column(name = "ATTR19")
    private String attr19;

    /**
     * 备用字段20
     */
    @Column(name = "ATTR20")
    private String attr20;

    /**
     * 备用字段21
     */
    @Column(name = "ATTR21")
    private String attr21;

    /**
     * 备用字段22
     */
    @Column(name = "ATTR22")
    private String attr22;

    /**
     * 备用字段23
     */
    @Column(name = "ATTR23")
    private String attr23;

    /**
     * 备用字段24
     */
    @Column(name = "ATTR24")
    private String attr24;

    /**
     * 备用字段25
     */
    @Column(name = "ATTR25")
    private String attr25;

    /**
     * 备用字段26
     */
    @Column(name = "ATTR26")
    private String attr26;

    /**
     * 备用字段27
     */
    @Column(name = "ATTR27")
    private String attr27;

    /**
     * 备用字段28
     */
    @Column(name = "ATTR28")
    private String attr28;

    /**
     * 备用字段29
     */
    @Column(name = "ATTR29")
    private String attr29;

    /**
     * 备用字段30
     */
    @Column(name = "ATTR30")
    private String attr30;


}
