package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceBillTaxRuleDto</p>
 * <p>Description: 企业账单进项税规则配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 7:35 PM
 */
@Data
public class FinanceBillTaxRuleDto {

    private String id;

    private String companyId;

    private String bizCode;

    private String bizName;

    /**
     * 是否计算进项税
     */
    private Boolean isCalculation;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 税率
     */
    private Integer taxes;

    private Boolean isUpdate;

    private Integer state;
}
