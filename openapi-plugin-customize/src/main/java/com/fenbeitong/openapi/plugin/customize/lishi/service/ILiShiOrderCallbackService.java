package com.fenbeitong.openapi.plugin.customize.lishi.service;

/**
 * <p>Title: ILiShiOrderCallbackService</p>
 * <p>Description: 理士订单回传服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/11 11:47 AM
 */
public interface ILiShiOrderCallbackService {

    /**
     * 订单回传
     *
     * @param configId 数据转换配置id
     * @param data     订单数据
     * @return 回传结果
     */
    Object callback(Long configId, String data);
}
