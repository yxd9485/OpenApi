package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;

public interface IYunzhijiaCorpService {

    /**
     * 根据企业id获取企业信息
     *
     * @param corpId 钉钉企业id
     * @return 企业信息
     */
    PluginCorpDefinition getByCorpId(String corpId);

    /**
     * 根据企业id获取企业信息
     *
     * @param companyId 分贝企业id
     * @return 企业信息
     */
    PluginCorpDefinition getByCompanyId(String companyId);
}
