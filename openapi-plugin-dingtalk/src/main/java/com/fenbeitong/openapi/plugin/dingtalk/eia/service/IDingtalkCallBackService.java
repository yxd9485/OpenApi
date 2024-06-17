package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import java.util.List;


public interface IDingtalkCallBackService {

    void register(String corpId, String[] callbackTags, String callbackDomain);


    void update(String corpId, String[] callbackTags, String callbackDomain);
    void delete(String corpId);
    List<String> list(String corpId);
}
