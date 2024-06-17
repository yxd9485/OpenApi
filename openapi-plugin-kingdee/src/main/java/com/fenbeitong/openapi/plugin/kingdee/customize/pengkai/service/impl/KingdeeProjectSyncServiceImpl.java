package com.fenbeitong.openapi.plugin.kingdee.customize.pengkai.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.kingdee.customize.pengkai.dto.SupportingInformationDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.pengkai.service.KingdeeProjectSyncService;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.finhub.framework.core.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName KingdeeProjectSyncServiceImpl
 * @Description 鹏凯金蝶辅助资料拉取
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/7 上午10:52
 **/
@Service
@Slf4j
public class KingdeeProjectSyncServiceImpl implements KingdeeProjectSyncService {

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Value("${host.connector}")
    private String connectorHost;

    private static final String CONNECTOR_PROJECT = "connector_project_setting";

    @Override
    public void syncKingdeeProject(String companyId) {

        //从open_msg_setup表中取出连接器配置
        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, CONNECTOR_PROJECT);
        if (ObjectUtils.isEmpty(openMsgSetup) || StringUtils.isEmpty(openMsgSetup.getStrVal1())) {
            log.info("金蝶项目同步连接器配置为空，请检查，公司id:{},itemCode:{}", companyId, CONNECTOR_PROJECT);
            throw new OpenApiArgumentException("找不到金蝶项目同步连接器配置信息");
        }
        SupportingInformationDTO connectorParam = JsonUtils.toObj(openMsgSetup.getStrVal1(), SupportingInformationDTO.class);
        if (ObjectUtils.isEmpty(connectorParam) || StringUtils.isEmpty(connectorParam.getInteractiveId())) {
            log.info("金蝶项目同步连接器配置缺失，请检查，companyId:{},connectorParam:{}", companyId, JsonUtils.toJson(connectorParam));
            throw new OpenApiArgumentException("找不到金蝶项目同步连接器配置信息");
        }
        int pageIndex = ObjectUtils.isEmpty(connectorParam.getPageIndex()) ? 1 : connectorParam.getPageIndex();
        int pageSize = ObjectUtils.isEmpty(connectorParam.getPageSize()) ? 100 : connectorParam.getPageSize();
        //连接器入参
        Map<String,Object> connectorParams = new HashMap<>();
        Map<String,Object> bodyParams = new HashMap<>();
        bodyParams.put("companyId",companyId);
        bodyParams.put("startRow",(pageIndex-1)*pageSize );
        bodyParams.put("limit", pageSize);
        connectorParams.put("companyId", companyId);
        connectorParams.put("connectorId", connectorParam.getInteractiveId());
        connectorParams.put("needTurnPage", "1");
        connectorParams.put("bodyParams", JsonUtils.toJson(bodyParams));

        log.info("调用连接器同步项目入参:"+JsonUtils.toJson(connectorParams));
        //调用连接器同步项目
        String result = RestHttpUtils.postJson(connectorHost + "/connector/data", null, JsonUtils.toJson(connectorParams));
        BaseDTO syncProjectRes = JsonUtils.toObj(result, BaseDTO.class);
        if (syncProjectRes == null || !syncProjectRes.success()) {
            String msg = syncProjectRes == null ? "" : Optional.ofNullable(syncProjectRes.getMsg()).orElse("");
            log.info("鹏凯金蝶项目拉取失败，参数：{},失败信息：{}", JsonUtils.toJson(connectorParam), msg);
            throw new FinhubException(-9999, "鹏凯金蝶项目拉取失败,失败信息：" + msg);
        }
    }
}
