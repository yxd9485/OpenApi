package com.fenbeitong.openapi.plugin.daoyiyun.service;

import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunUserInfoRespDTO;

/**
 * 道一云员工信息
 *
 * @author lizhen
 */
public interface DaoYiYunUserService {

    /**
     * 查询用户账号
     *
     * @param userId
     * @param applicationId
     * @return
     */
    String getUserAccount(String userId, String applicationId);

    /**
     * 查询用户信息
     *
     * @param userId
     * @param applicationId
     * @return
     */
    DaoYiYunUserInfoRespDTO.UserInfo getUserInfo(String userId, String applicationId);

    /**
     * 使用account查询用户ID
     * @param account
     * @param applicationId
     * @return
     */
    String getUserId(String account, String applicationId);

    /**
     * 使用account查询用户信息
     * @param account
     * @param applicationId
     * @return
     */
    DaoYiYunUserInfoRespDTO.UserInfo getUserInfoByAccount(String account, String applicationId);

}
