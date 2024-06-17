package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.corp;

import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
public class YunzhijiaCorpServiceImpl implements IYunzhijiaCorpService {
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
