package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>Title: WawjBillSummaryDto</p>
 * <p>Description: 我爱我家账单汇总数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/10 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WawjBillSummaryDto {

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 批次id
     */
    private String batchId;

    /**
     * 批次行id
     */
    private Integer batchLineId;

    /**
     * 状态 -1:初始化;0:成功;1:失败
     */
    private Integer status;

    /**
     * 响应信息
     */
    private String respMsg;

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
    private String unitCode;

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
     * 收款对象
     */
    private String payeeCategory;

    /**
     * 收款方代码
     */
    private String payeeCode;

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
