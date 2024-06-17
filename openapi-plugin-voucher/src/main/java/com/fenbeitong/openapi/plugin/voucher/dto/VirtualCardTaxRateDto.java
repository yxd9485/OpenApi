package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: VirtualCardTaxRateDto</p>
 * <p>Description: 虚拟卡税率配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:21 PM
 */
@Data
public class VirtualCardTaxRateDto {

    private String id;

    private String companyId;

    /**
     * 发票类型名称
     */
    private String invoiceType;

    /**
     * 发票类型编码
     */
    private Integer code;

    /**
     * 是否可抵扣
     */
    private Boolean isDeduction;

    /**
     * 税金计算方式 1:票面税率	2:系统折算
     */
    private Integer taxesComputeMode;

    /**
     * 税率
     */
    private BigDecimal taxes;

}
