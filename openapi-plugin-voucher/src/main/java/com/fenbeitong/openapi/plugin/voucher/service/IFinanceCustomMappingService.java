package com.fenbeitong.openapi.plugin.voucher.service;

import java.util.Map;

/**
 * <p>Title: IFinanceCustomMappingService</p>
 * <p>Description: 自定义映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/30 11:16 AM
 */
public interface IFinanceCustomMappingService {

    Map<String, Object> loadCustomMapping(String companyId);
}
