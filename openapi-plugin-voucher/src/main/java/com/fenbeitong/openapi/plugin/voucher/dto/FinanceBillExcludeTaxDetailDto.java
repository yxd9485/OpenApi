package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceBillExcludeTaxDetailDto</p>
 * <p>Description: 不计税部门/项目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:08 PM
 */
@Data
public class FinanceBillExcludeTaxDetailDto {

    private String id;

    private String orgCostId;

    private String orgCostName;

    private String orgCostInfo;
}
