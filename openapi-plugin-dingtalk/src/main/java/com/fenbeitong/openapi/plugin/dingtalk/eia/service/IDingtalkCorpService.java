package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;

/**
 * <p>Title: IDingtalkCorpService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 5:58 PM
 */
public interface IDingtalkCorpService {

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
