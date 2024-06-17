package com.fenbeitong.openapi.plugin.customize.power.service;

/**
 * @author zhangjindong
 */
public interface PowerOrderCallBackService {

    /**
     * 活力二八订单回传
     */
     Object callBackOrderData(String callbackData,String  companyId);
}
