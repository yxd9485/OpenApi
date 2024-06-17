package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: VirtualCardTaxRateRespDto</p>
 * <p>Description: 虚拟卡税率配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:20 PM
 */
@Data
public class VirtualCardTaxRateRespDto extends FinanceBaseRespDto {

    private List<VirtualCardTaxRateDto> data;
}
