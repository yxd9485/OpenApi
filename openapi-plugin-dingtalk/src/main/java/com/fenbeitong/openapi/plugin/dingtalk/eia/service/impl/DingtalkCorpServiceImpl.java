package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: DingtalkCorpServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 5:58 PM
 */
@ServiceAspect
@Service
public class DingtalkCorpServiceImpl implements IDingtalkCorpService {

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Override
    public PluginCorpDefinition getByCorpId(String corpId) {
        return pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
    }

    @Override
    public PluginCorpDefinition getByCompanyId(String companyId) {
        return pluginCorpDefinitionDao.getByCompanyId(companyId);
    }
}
