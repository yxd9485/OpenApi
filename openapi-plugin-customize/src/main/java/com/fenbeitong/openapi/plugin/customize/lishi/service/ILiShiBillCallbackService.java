package com.fenbeitong.openapi.plugin.customize.lishi.service;

/**
 * <p>Title: ILiShiBillCallbackService</p>
 * <p>Description: 理士帐单回传服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/6/03
 */
public interface ILiShiBillCallbackService {

    /**
     * 帐单回传接口
     *
     * @param configId 数据转换配置id
     * @param data     bill数据
     * @return 回传结果
     */
    Object callback(Long configId, String data, String userName, String password, String param) ;
}
