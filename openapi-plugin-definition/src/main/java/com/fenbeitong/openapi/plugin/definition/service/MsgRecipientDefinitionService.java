package com.fenbeitong.openapi.plugin.definition.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.message.MsgRecipientInfoDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.definition.util.DefinitionCheckUtils;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.dao.MsgRecipientDefinitionDao;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.entity.MsgRecipientDefinition;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

import static com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode.LACK_NECESSARY_PARAM;

/**
 * 三方插件消息接收配置（钉钉/企业微信）
 * Created by log.chang on 2019/12/25.
 */
@ServiceAspect
@Service
public class MsgRecipientDefinitionService {

    @Autowired
    private MsgRecipientDefinitionDao msgRecipientDefinitionDao;
    @Autowired
    private AuthDefinitionDao authDefinitionDao;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    public MsgRecipientInfoDTO createMsgRecipientDefinition(String thirdCorpId, String thirdAgentId, String thirdUserId, String thirdUserName) {
        preCreateMsgRecipientDefinition(thirdCorpId, thirdAgentId, thirdUserId, thirdUserName);
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(thirdCorpId);
        DefinitionCheckUtils.checkPluginCorpDefinition(thirdCorpId, pluginCorpDefinition);
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(pluginCorpDefinition.getAppId());
        DefinitionCheckUtils.checkAuthDefinition(pluginCorpDefinition.getAppId(), authDefinition);
        Date now = DateUtils.now();
        MsgRecipientDefinition msgRecipientDefinition = MsgRecipientDefinition.builder()
                .thirdCorpId(thirdCorpId)
                .thirdAgentId(thirdAgentId)
                .thirdUserId(thirdUserId)
                .thirdUserName(thirdUserName)
                .createTime(now)
                .updateTime(now)
                .description(authDefinition.getAppName())
                .status(1)
                .build();
        msgRecipientDefinitionDao.saveSelective(msgRecipientDefinition);
        return MsgRecipientInfoDTO.builder()
                .thirdCorpId(msgRecipientDefinition.getThirdCorpId())
                .thirdAgentId(msgRecipientDefinition.getThirdAgentId())
                .thirdUserId(msgRecipientDefinition.getThirdUserId())
                .thirdUserName(msgRecipientDefinition.getThirdUserName())
                .build();
    }

    private void preCreateMsgRecipientDefinition(String thirdCorpId, String thirdAgentId, String thirdUserId, String thirdUserName) {
        if (StringUtils.isTrimBlank(thirdCorpId))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "thirdCorpId");
        if (StringUtils.isTrimBlank(thirdAgentId))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "thirdAgentId");
        if (StringUtils.isTrimBlank(thirdUserId))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "thirdUserId");
        if (StringUtils.isTrimBlank(thirdUserName))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "thirdUserName");
    }

}
