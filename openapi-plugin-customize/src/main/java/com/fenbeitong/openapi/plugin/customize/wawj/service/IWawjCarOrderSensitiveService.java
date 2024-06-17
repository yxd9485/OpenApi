package com.fenbeitong.openapi.plugin.customize.wawj.service;

/**
 * <p>Title: IWawjCarOrderSensitiveService</p>
 * <p>Description: 我爱我家用车敏感订单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/5 11:53 AM
 */
public interface IWawjCarOrderSensitiveService {

    /**
     * 设置用车敏感订单
     *
     * @param companyId 公司id
     */
    void setSensitive(String companyId, int day);
}
