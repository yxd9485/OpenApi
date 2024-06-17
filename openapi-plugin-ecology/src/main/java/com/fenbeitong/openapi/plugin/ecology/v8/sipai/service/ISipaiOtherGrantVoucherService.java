package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service;

/**
 * <p>Title: ISipaiOtherGrantVoucherService</p>
 * <p>Description: 思派其他发券服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/4 9:51 PM
 */
public interface ISipaiOtherGrantVoucherService {

    /**
     * 思派周末加班发券
     *
     * @param companyId
     * @param ruleId    规则id
     */
    void grantWeekendOverTimeVoucher(String companyId, Long ruleId);
}
