package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.dingtalk.api.response.OapiRoleListResponse;

/**
 * 钉钉角色
 *
 * @author lizhen
 * @date 2020/10/23
 */
public interface IDingtalkIsvRoleService {
    /**
     * 获取角色列表
     * @param corpId
     */
    OapiRoleListResponse listRole(String corpId);
}
