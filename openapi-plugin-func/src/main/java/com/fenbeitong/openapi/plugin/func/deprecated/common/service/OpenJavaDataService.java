package com.fenbeitong.openapi.plugin.func.deprecated.common.service;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/9 22:31
 * @since 1.0
 */
public interface OpenJavaDataService {

    /**
     * 根据订单ID查询订单自定义字段参数
     * @param orderId
     * @return
     */
    String getOrderParam(String orderId);
}
