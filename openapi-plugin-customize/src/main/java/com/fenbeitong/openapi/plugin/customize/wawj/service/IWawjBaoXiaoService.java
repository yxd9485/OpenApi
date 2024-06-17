package com.fenbeitong.openapi.plugin.customize.wawj.service;

import java.util.List;

/**
 * <p>Title: IWawjBaoXiaoService</p>
 * <p>Description: 我爱我家报销服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/19 10:32 AM
 */
public interface IWawjBaoXiaoService {

    /**
     * 推送报销单到我爱我家
     *
     * @param companyId   公司id
     * @param batchIdList 批次编号id列表
     */
    void push(String companyId, List<String> batchIdList) throws Exception;
}
