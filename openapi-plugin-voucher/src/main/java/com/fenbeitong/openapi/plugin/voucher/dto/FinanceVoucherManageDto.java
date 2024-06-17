package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceVoucherManageDto</p>
 * <p>Description: 凭证管理通用配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/5 8:08 PM
 */
@Data
public class FinanceVoucherManageDto {

    private String id;

    private String companyId;

    private Integer generateType;

    /**
     * 税金合并方式 1:税金合并 2:税金拆分
     */
    private Integer taxesCombineType;

    /**
     * 借方合并方式  1:正常合并   2:完全不合并
     */
    private Integer borrowerCombineType;

    private Integer state;
}
