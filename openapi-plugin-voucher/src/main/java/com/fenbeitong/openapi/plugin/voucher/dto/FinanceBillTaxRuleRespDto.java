package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceBillTaxRuleRespDto</p>
 * <p>Description: 企业账单进项税规则配置响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 7:34 PM
 */
@Data
public class FinanceBillTaxRuleRespDto {

    private List<FinanceBillTaxRuleDto> data;
}
