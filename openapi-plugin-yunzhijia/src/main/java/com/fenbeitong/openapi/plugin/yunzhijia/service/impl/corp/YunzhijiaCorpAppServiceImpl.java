package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.corp;

import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaCorpAppServiceImpl implements IYunzhijiaCorpAppService {
    @Autowired
    PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    @Override
    public PluginCorpAppDefinition getByCorpId(String corpId) {
       return pluginCorpAppDefinitionDao.getByCorpId(corpId);
    }
}
