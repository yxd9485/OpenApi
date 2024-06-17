package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName IBankPaymentService
 * @Description 辰光融信对公付款接口
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/20
 **/
public interface IBankPaymentService {
    /**
     * 对公付款申请单推送
     *
     * @param request   参数
     * @param companyId 公司id
     */
    void pushData(HttpServletRequest request, String companyId);
}
