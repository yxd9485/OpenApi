package com.fenbeitong.openapi.plugin.definition.util;

import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;

import static com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode.APP_ID_UNAUTH;
import static com.fenbeitong.openapi.plugin.definition.constant.DefinitionRespCode.CORP_ID_UNKNOWN;

/**
 * 配置检查工具类
 * Created by log.chang on 2019/12/25.
 */
public class DefinitionCheckUtils {

    public static void checkPluginCorpDefinition(String corpId, PluginCorpDefinition pluginCorpDefinition) {
        if (pluginCorpDefinition == null)
            throw new OpenApiDefinitionException(CORP_ID_UNKNOWN, corpId);
    }

    public static void checkAuthDefinition(String appId, AuthDefinition authDefinition) {
        if (authDefinition == null)
            throw new OpenApiDefinitionException(APP_ID_UNAUTH, appId);
    }

}
