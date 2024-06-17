package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaohai
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunParseFormApplyService  {

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    public CommonApplyReqDTO parseTripApprovalForm(DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO, String companyId) {
        OpenThirdScriptConfig
            scriptConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.TRIP_APPLY_SYNC);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("daoYiYunCallbackBodyDTO", daoYiYunCallbackBodyDTO);
        }};
        if (!StringUtils.isBlank(scriptConfig.getParamJson())) {
            Map<String, Object> param = JsonUtils.toObj(scriptConfig.getParamJson(), new TypeReference<Map<String, Object>>() {
            });
            params.putAll(param);
        }
        CommonApplyReqDTO commonApplyReqDTO = (CommonApplyReqDTO) EtlUtils.execute(scriptConfig.getScript(), params);
        return commonApplyReqDTO;
    }


}
