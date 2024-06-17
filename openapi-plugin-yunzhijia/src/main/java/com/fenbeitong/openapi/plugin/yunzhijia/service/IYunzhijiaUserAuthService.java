package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;

/**
 * <p>Title: IYunzhijiaUserAuthService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/4/30 3:35 下午
 */
public interface IYunzhijiaUserAuthService {
    /**
     * 用户免登
     * @param corpId
     * @param appId
     * @param ticket
     * @return
     */
    LoginResVO userAuth(String corpId, String appId, String ticket);
}
