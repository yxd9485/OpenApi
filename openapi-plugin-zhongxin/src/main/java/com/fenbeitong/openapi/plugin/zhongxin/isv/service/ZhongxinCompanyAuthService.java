package com.fenbeitong.openapi.plugin.zhongxin.isv.service;

import com.alibaba.fastjson.JSONObject;

public interface ZhongxinCompanyAuthService {

    JSONObject companyAuth(String requestBody);

    void getMsg(String corpId, String userId);

    String verify(String corpId, String name, String verifyCode);
}
