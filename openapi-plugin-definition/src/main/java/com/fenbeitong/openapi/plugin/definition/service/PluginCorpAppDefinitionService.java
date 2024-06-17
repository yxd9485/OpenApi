package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

/**
 * 企业插件集成三方应用配置
 * Created by log.chang on 2019/12/24.
 */
@ServiceAspect
@Service
public class PluginCorpAppDefinitionService {


    @Autowired
    private PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    /**
     * 保存插件集成企业三方应用配置信息
     */
    public PluginCorpAppDefinition createPluginCorpAppDefinition(String thirdCorpId, String thirdAppKey, String thirdAppSecret,
                                                                 String thirdAppName, Long thirdAgentId, Date now) {
        PluginCorpAppDefinition pluginCorpAppDefinition = PluginCorpAppDefinition.builder()
                .thirdCorpId(thirdCorpId)
                .thirdAppKey(thirdAppKey)
                .thirdAppSecret(thirdAppSecret)
                .thirdAppName(thirdAppName)
                .thirdAgentId(thirdAgentId)
                .createTime(now)
                .updateTime(now)
                .build();
        pluginCorpAppDefinitionDao.saveSelective(pluginCorpAppDefinition);
        return pluginCorpAppDefinition;
    }

}
