package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service;

/**
 * <p>Title: ISipaiApplyService</p>
 * <p>Description: 思派行程用车审批服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/30 6:47 PM
 */
public interface ISipaiApplyService {

    /**
     * 生成行程审批或用车审批单
     *
     * @param companyId 公司id
     */
    void createApply(String companyId);
}
