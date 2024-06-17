package com.fenbeitong.openapi.plugin.zhongxin.isv.service;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;

public interface ZhongxinUserService {
    void userAdd(String encryptMsg);

    String getCompanyName(String encryptMsg);

    LoginResVO authLogin(String hash);
}
