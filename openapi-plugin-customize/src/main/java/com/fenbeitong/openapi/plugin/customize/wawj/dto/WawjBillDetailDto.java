package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>Title: WawjBillDetailDto</p>
 * <p>Description: 我爱我家账单明细</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/10 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WawjBillDetailDto {

    /**
     * 批次id
     */
    private String batchId;

    /**
     * 分组条件md5值
     */
    private String md5Value;

    /**
     * 公司编码
     */
    private String companyCode;

    /**
     * 报销日期
     */
    private String reportDate;

    /**
     * 报销人
     */
    private String employeeCode;

    /**
     * 法人公司
     */
    private String incorporatedCompany;

    /**
     * 核算单位
     */
    private String accountCompanyCode;

    /**
     * 头描述
     */
    private String description;

    /**
     * 行描述
     */
    private String lineDescription;

    /**
     * 报销类型编码
     */
    private String expenseTypeCode;

    /**
     * 费用项目编码
     */
    private String expenseItemCode;

    /**
     * 税费类型 ps:3%增值税-经营类-10
     */
    private String taxTypeCode;

    /**
     * 报销金额
     */
    private BigDecimal reportAmount;

    /**
     * 税率
     */
    private String taxRate;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 汇总部门编码
     */
    private String summaryDeptCode;

    /**
     * 项目
     */
    private String dimension2Code;

    /**
     * 门店
     */
    private String dimension3Code;

    /**
     * 业务类型
     */
    private String dimension4Code;

    /**
     * 场景类型编码
     */
    private Integer orderCategoryType;

    /**
     * 场景类型名称
     */
    private String orderCategoryName;

    /**
     * 订单类型1:因公;2:因私
     */
    private Integer orderType;

    /**
     * 订单日期
     */
    private String orderDate;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 原始订单id
     */
    private String sourceOrderId;

    /**
     * 票号
     */
    private String ticketNo;

    /**
     * 账单id
     */
    private String billId;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 账单明细id
     */
    private String billDetailId;

    /**
     * 备用字段1
     */
    private String attribute1;

    /**
     * 备用字段2
     */
    private String attribute2;

    /**
     * 备用字段3
     */
    private String attribute3;

    /**
     * 备用字段4
     */
    private String attribute4;

    /**
     * 备用字段5
     */
    private String attribute5;

    /**
     * 备用字段6
     */
    private String attribute6;

    /**
     * 备用字段7
     */
    private String attribute7;

    /**
     * 备用字段8
     */
    private String attribute8;

    /**
     * 备用字段9
     */
    private String attribute9;

    /**
     * 备用字段10
     */
    private String attribute10;

    /**
     * 备用字段11
     */
    private String attribute11;

    /**
     * 备用字段12
     */
    private String attribute12;

    /**
     * 备用字段13
     */
    private String attribute13;

    /**
     * 备用字段14
     */
    private String attribute14;

    /**
     * 备用字段15
     */
    private String attribute15;

    /**
     * 备用字段16
     */
    private String attribute16;

    /**
     * 备用字段17
     */
    private String attribute17;

    /**
     * 备用字段18
     */
    private String attribute18;

    /**
     * 备用字段19
     */
    private String attribute19;

    /**
     * 备用字段20
     */
    private String attribute20;

}
