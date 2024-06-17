package com.fenbeitong.openapi.plugin.customize.cocacola.service;

import com.fenbeitong.openapi.plugin.customize.cocacola.dto.ColaAltmanOrderDTO;

import java.util.Map;

/**
 * 可口可乐万能订单转换
 *
 * @author ctl
 * @date 2021/11/19
 */
public interface ColaAltmanOrderTranService {

    /**
     * 订单详情转换
     *
     * @param sourceStr
     * @return
     */
    Map<String, Object> tran(String sourceStr);

    /**
     * 推送可乐订单
     *
     * @param data
     * @param companyId
     */
    void push(ColaAltmanOrderDTO data, String companyId);
}
