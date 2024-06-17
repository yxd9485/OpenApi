package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceConfigRespDto</p>
 * <p>Description: 财务配置响应类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 5:18 PM
 */
@Data
public class FinanceConfigRespDto extends FinanceBaseRespDto{

    private FinanceConfigDto data;
}
