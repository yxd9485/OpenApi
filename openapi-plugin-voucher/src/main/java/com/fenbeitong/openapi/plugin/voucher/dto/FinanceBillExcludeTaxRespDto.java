package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceBillExcludeTaxRespDto</p>
 * <p>Description: 不计税部门及项目响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:05 PM
 */
@Data
public class FinanceBillExcludeTaxRespDto extends FinanceBaseRespDto{

    private FinanceBillExcludeTaxDto data;
}
