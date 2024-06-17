package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;

import java.util.List;

/** 
 * @Description  
 * @Author duhui
 * @Date  2021-04-07
**/
public interface IApiIsvUserService {

    /**
     * 根据钉钉部门ID，获取其下的所有用户
     *
     * @param departmentId 钉钉部门ID
     * @param corpId       corpId
     * @return 钉钉用户列表
     */
    List<DingtalkUser> getAllUserByDepartment(long departmentId, String corpId);

    /**
     * 根据corpId和授权码获取钉钉登录用户ID
     *
     * @param corpId   corpId
     * @param authCode 登录授权码
     * @return 钉钉登录用户ID
     */
    String getAuthUserId(String corpId, String authCode);
}
