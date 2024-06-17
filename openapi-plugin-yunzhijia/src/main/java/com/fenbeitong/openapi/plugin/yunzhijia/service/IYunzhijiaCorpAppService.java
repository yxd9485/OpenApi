package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;

public interface IYunzhijiaCorpAppService {

    /**
     * 根据钉钉公司id获取公司app
     *
     * @param corpId 钉钉公司id
     * @return 公司app
     */
    PluginCorpAppDefinition getByCorpId(String corpId);
}
