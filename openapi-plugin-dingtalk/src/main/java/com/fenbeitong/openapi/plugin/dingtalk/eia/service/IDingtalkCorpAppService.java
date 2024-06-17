package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;

/**
 * <p>Title: IDingtalkCorpAppService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 11:29 AM
 */
public interface IDingtalkCorpAppService {

    /**
     * 根据钉钉公司id获取公司app
     *
     * @param corpId 钉钉公司id
     * @return 公司app
     */
    PluginCorpAppDefinition getByCorpId(String corpId);
}
