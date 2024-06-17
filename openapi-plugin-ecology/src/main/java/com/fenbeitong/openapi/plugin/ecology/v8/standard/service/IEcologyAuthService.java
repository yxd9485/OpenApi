package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;

/**
 * 泛微授权服务
 * @Auther zhang.peng
 * @Date 2022/1/11
 */

public interface IEcologyAuthService {

    /**
     * 获取用户
     * @param companyId 公司id
     * @param phone 手机号
     * @return 登录信息
     */
    LoginResVO getLoginInfo(String companyId , String phone );
}
