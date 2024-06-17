package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;

/**
 * 致远授权登录接口
 * @Auther xiaohai
 * @Date 2022/09/27
 */

public interface ISeeyonAuthService {

    /**
     * 获取用户
     * @param authCode 加密字符串，包含手机号和公司id
     * @return 登录信息
     */
    LoginResVO getLoginInfo( String authCode );
}
