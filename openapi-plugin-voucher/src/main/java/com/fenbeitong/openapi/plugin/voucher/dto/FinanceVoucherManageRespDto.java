package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceVoucherManageRespDto</p>
 * <p>Description: 凭证管理通用配置响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/5 8:12 PM
 */
@Data
public class FinanceVoucherManageRespDto extends FinanceBaseRespDto {

    private FinanceVoucherManageDto data;
}
