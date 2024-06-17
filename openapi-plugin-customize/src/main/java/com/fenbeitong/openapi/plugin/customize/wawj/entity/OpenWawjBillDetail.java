package com.fenbeitong.openapi.plugin.customize.wawj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>Title: OpenWawjBillDetail</p>
 * <p>Description: 我爱我家账单表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/10 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_wawj_bill_detail")
public class OpenWawjBillDetail {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 批次id
     */
    @Column(name = "BATCH_ID")
    private String batchId;

    /**
     * 分组条件md5值
     */
    @Column(name = "MD5VALUE")
    private String md5Value;

    /**
     * 公司编码
     */
    @Column(name = "COMPANY_CODE")
    private String companyCode;

    /**
     * 报销日期
     */
    @Column(name = "REPORT_DATE")
    private String reportDate;

    /**
     * 报销人
     */
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    /**
     * 法人公司
     */
    @Column(name = "INCORPORATED_COMPANY")
    private String incorporatedCompany;

    /**
     * 核算单位
     */
    @Column(name = "ACCOUNT_COMPANY_CODE")
    private String accountCompanyCode;

    /**
     * 头描述
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 行描述
     */
    @Column(name = "LINE_DESCRIPTION")
    private String lineDescription;

    /**
     * 报销类型编码
     */
    @Column(name = "EXPENSE_TYPE_CODE")
    private String expenseTypeCode;

    /**
     * 费用项目编码
     */
    @Column(name = "EXPENSE_ITEM_CODE")
    private String expenseItemCode;

    /**
     * 税费类型 ps:3%增值税-经营类-10
     */
    @Column(name = "TAX_TYPE_CODE")
    private String taxTypeCode;

    /**
     * 报销金额
     */
    @Column(name = "REPORT_AMOUNT")
    private BigDecimal reportAmount;

    /**
     * 税率
     */
    @Column(name = "TAX_RATE")
    private String taxRate;

    /**
     * 税额
     */
    @Column(name = "TAX_AMOUNT")
    private BigDecimal taxAmount;

    /**
     * 部门编码
     */
    @Column(name = "DEPT_CODE")
    private String deptCode;

    /**
     * 汇总部门编码
     */
    @Column(name = "SUMMARY_DEPT_CODE")
    private String summaryDeptCode;

    /**
     * 项目
     */
    @Column(name = "DIMENSION2_CODE")
    private String dimension2Code;

    /**
     * 门店
     */
    @Column(name = "DIMENSION3_CODE")
    private String dimension3Code;

    /**
     * 业务类型
     */
    @Column(name = "DIMENSION4_CODE")
    private String dimension4Code;

    /**
     * 场景类型编码
     */
    @Column(name = "ORDER_CATEGORY_TYPE")
    private Integer orderCategoryType;

    /**
     * 场景类型名称
     */
    @Column(name = "ORDER_CATEGORY_NAME")
    private String orderCategoryName;

    /**
     * 订单类型1:因公;2:因私
     */
    @Column(name = "ORDER_TYPE")
    private Integer orderType;

    /**
     * 订单日期
     */
    @Column(name = "ORDER_DATE")
    private String orderDate;

    /**
     * 订单id
     */
    @Column(name = "ORDER_ID")
    private String orderId;

    /**
     * 原始订单id
     */
    @Column(name = "SOURCE_ORDER_ID")
    private String sourceOrderId;

    /**
     * 票号
     */
    @Column(name = "TICKET_NO")
    private String ticketNo;

    /**
     * 账单id
     */
    @Column(name = "BILL_ID")
    private String billId;

    /**
     * 账单编号
     */
    @Column(name = "BILL_NO")
    private String billNo;

    /**
     * 账单明细id
     */
    @Column(name = "BILL_DETAIL_ID")
    private String billDetailId;

    /**
     * 备用字段1
     */
    @Column(name = "ATTRIBUTE1")
    private String attribute1;

    /**
     * 备用字段2
     */
    @Column(name = "ATTRIBUTE2")
    private String attribute2;

    /**
     * 备用字段3
     */
    @Column(name = "ATTRIBUTE3")
    private String attribute3;

    /**
     * 备用字段4
     */
    @Column(name = "ATTRIBUTE4")
    private String attribute4;

    /**
     * 备用字段5
     */
    @Column(name = "ATTRIBUTE5")
    private String attribute5;

    /**
     * 备用字段6
     */
    @Column(name = "ATTRIBUTE6")
    private String attribute6;

    /**
     * 备用字段7
     */
    @Column(name = "ATTRIBUTE7")
    private String attribute7;

    /**
     * 备用字段8
     */
    @Column(name = "ATTRIBUTE8")
    private String attribute8;

    /**
     * 备用字段9
     */
    @Column(name = "ATTRIBUTE9")
    private String attribute9;

    /**
     * 备用字段10
     */
    @Column(name = "ATTRIBUTE10")
    private String attribute10;

    /**
     * 备用字段11
     */
    @Column(name = "ATTRIBUTE11")
    private String attribute11;

    /**
     * 备用字段12
     */
    @Column(name = "ATTRIBUTE12")
    private String attribute12;

    /**
     * 备用字段13
     */
    @Column(name = "ATTRIBUTE13")
    private String attribute13;

    /**
     * 备用字段14
     */
    @Column(name = "ATTRIBUTE14")
    private String attribute14;

    /**
     * 备用字段15
     */
    @Column(name = "ATTRIBUTE15")
    private String attribute15;

    /**
     * 备用字段16
     */
    @Column(name = "ATTRIBUTE16")
    private String attribute16;

    /**
     * 备用字段17
     */
    @Column(name = "ATTRIBUTE17")
    private String attribute17;

    /**
     * 备用字段18
     */
    @Column(name = "ATTRIBUTE18")
    private String attribute18;

    /**
     * 备用字段19
     */
    @Column(name = "ATTRIBUTE19")
    private String attribute19;

    /**
     * 备用字段20
     */
    @Column(name = "ATTRIBUTE20")
    private String attribute20;

}
