package com.fenbeitong.openapi.plugin.daoyiyun.service;

/**
 * 道一云token
 * @author lizhen
 */
public interface DaoYiYunTokenService {

    /**
     * 获取token
     * @param applicationId
     * @return
     */
    String getAccessToken(String applicationId);

    /**
     * 清除token
     * @param applicationId
     */
    void clearTenantAccessToken(String applicationId);

}
