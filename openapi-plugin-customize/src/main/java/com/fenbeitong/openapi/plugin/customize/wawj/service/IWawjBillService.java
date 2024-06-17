package com.fenbeitong.openapi.plugin.customize.wawj.service;

/**
 * <p>Title: IWawjBillService</p>
 * <p>Description: 我爱我家账单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/9 7:39 PM
 */
public interface IWawjBillService {

    /**
     * 我爱我家账单保存
     *
     * @param companyId 公司id
     * @param billNo    账单编号
     * @param delete    删除历史数据
     */
    void save(String companyId, String billNo, Integer delete);
}
