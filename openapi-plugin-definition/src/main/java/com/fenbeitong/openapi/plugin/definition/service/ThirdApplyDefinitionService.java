package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.definition.dto.plugin.apply.ThirdApplyDefinitionInfoDTO;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

import static com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode.LACK_NECESSARY_PARAM;

/**
 * 企业插件配置-三方应用审批与分贝通开放平台关系
 * Created by log.chang on 2019/12/25.
 */
@ServiceAspect
@Service
public class ThirdApplyDefinitionService {

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    public ThirdApplyDefinitionInfoDTO createThirdApplyDefinition(String thirdProcessCode, String thirdProcessName, Integer processType, String appId) {
        preCreateThirdApplyDefinition(thirdProcessCode, thirdProcessName, processType, appId);
        Date now = DateUtils.now();
        ThirdApplyDefinition thirdApplyDefinition = ThirdApplyDefinition.builder()
                .thirdProcessCode(thirdProcessCode)
                .thirdProcessName(thirdProcessName)
                .processType(processType)
                .appId(appId)
                .createTime(now)
                .updateTime(now)
                .build();
        thirdApplyDefinitionDao.save(thirdApplyDefinition);
        return ThirdApplyDefinitionInfoDTO.builder()
                .appId(appId)
                .thirdProcessCode(thirdProcessCode)
                .thirdProcessName(thirdProcessName)
                .processType(processType)
                .build();
    }

    private void preCreateThirdApplyDefinition(String thirdProcessCode, String thirdProcessName, Integer processType, String appId) {
        if (StringUtils.isTrimBlank(appId))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "appId");
        if (StringUtils.isTrimBlank(thirdProcessCode))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "thirdProcessCode");
        if (StringUtils.isTrimBlank(thirdProcessName))
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "thirdProcessName");
        if (processType == null)
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "processType");
    }

}
