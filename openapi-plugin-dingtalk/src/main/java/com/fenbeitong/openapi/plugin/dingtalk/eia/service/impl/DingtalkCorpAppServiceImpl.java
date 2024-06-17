package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpAppService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: DingtalkCorpAppServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 11:30 AM
 */
@ServiceAspect
@Service
public class DingtalkCorpAppServiceImpl implements IDingtalkCorpAppService {

    @Autowired
    PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    @Override
    public PluginCorpAppDefinition getByCorpId(String corpId) {
        return pluginCorpAppDefinitionDao.getByCorpId(corpId);
    }
}
