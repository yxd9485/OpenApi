package com.fenbeitong.openapi.plugin.voucher.service;

import com.fenbeitong.openapi.plugin.voucher.dto.FinanceGlobalConfigDto;

/**
 * <p>Title: IFinanceInitConfigService</p>
 * <p>Description: 凭证配置初始化类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/29 11:28 AM
 */
public interface IFinanceInitConfigService {

    /**
     * 初始化财务配置
     *
     * @param companyId 公司id
     * @param operateId 操作人id
     * @return 财务配置
     */
    FinanceGlobalConfigDto initConfig(String companyId, String operateId);
}
