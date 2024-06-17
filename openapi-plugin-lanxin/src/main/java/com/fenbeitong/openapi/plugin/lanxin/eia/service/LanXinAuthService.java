package com.fenbeitong.openapi.plugin.lanxin.eia.service;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;

/**
 * <p>Title: L</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/8 5:14 下午
 */
public interface LanXinAuthService {
    LoginResVO getLoginUser(String corpId, String authCode);
}
