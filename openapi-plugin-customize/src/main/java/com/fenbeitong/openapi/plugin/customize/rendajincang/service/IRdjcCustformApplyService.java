package com.fenbeitong.openapi.plugin.customize.rendajincang.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ICustformApplyService
 * @Description 人大金仓自定义申请单接口
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/10/12
 **/
public interface IRdjcCustformApplyService {
    /**
     * 过期自定义申请单推送（非行程、用车）
     *
     * @param request   参数
     * @param companyId 公司id
     */
    void pushExpiredData(HttpServletRequest request, String companyId);
}
