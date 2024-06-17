package com.fenbeitong.openapi.plugin.kingdee.customize.common.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.kingdee.customize.common.dto.ConnectorInfoDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.common.service.ConnectorSyncDataService;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.finhub.framework.core.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName ConnectorSyncDataServiceImpl
 * @Description 连接器同步数据公共方法
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/27 上午10:23
 **/
@Slf4j
@Service
public class ConnectorSyncDataServiceImpl implements ConnectorSyncDataService {

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Value("${host.connector}")
    private String connectorHost;

    @Override
    public void connectorSyncKingdeeData(String companyId, String settingCode) {
        //从open_msg_setup表中取出连接器配置
        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, settingCode);
        if (ObjectUtils.isEmpty(openMsgSetup) || StringUtils.isEmpty(openMsgSetup.getStrVal1())) {
            log.info("金蝶数据同步连接器配置为空，请检查，公司id:{},itemCode:{}", companyId, settingCode);
            throw new OpenApiArgumentException("找不到金蝶数据同步连接器配置信息");
        }
        ConnectorInfoDTO connectorParam = JsonUtils.toObj(openMsgSetup.getStrVal1(), ConnectorInfoDTO.class);
        if (ObjectUtils.isEmpty(connectorParam) || StringUtils.isEmpty(connectorParam.getInteractiveId())) {
            log.info("金蝶数据同步连接器配置缺失，请检查，companyId:{},connectorParam:{}", companyId, JsonUtils.toJson(connectorParam));
            throw new OpenApiArgumentException("找不到金蝶数据同步连接器配置信息");
        }
        int pageIndex = ObjectUtils.isEmpty(connectorParam.getPageIndex()) ? 1 : connectorParam.getPageIndex();
        int pageSize = ObjectUtils.isEmpty(connectorParam.getPageSize()) ? 100 : connectorParam.getPageSize();
        //连接器入参
        Map<String, Object> connectorParams = new HashMap<>();
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("companyId", companyId);
        bodyParams.put("startRow", (pageIndex - 1) * pageSize);
        bodyParams.put("limit", pageSize);
        connectorParams.put("companyId", companyId);
        connectorParams.put("connectorId", connectorParam.getInteractiveId());
        connectorParams.put("needTurnPage", "1");
        connectorParams.put("bodyParams", JsonUtils.toJson(bodyParams));

        log.info("调用连接器同步数据入参:" + JsonUtils.toJson(connectorParams));
        //调用连接器同步项目
        String result = RestHttpUtils.postJson(connectorHost + "/connector/data", null, JsonUtils.toJson(connectorParams));
        BaseDTO syncProjectRes = JsonUtils.toObj(result, BaseDTO.class);
        if (syncProjectRes == null || !syncProjectRes.success()) {
            String msg = syncProjectRes == null ? "" : Optional.ofNullable(syncProjectRes.getMsg()).orElse("");
            log.info("同步金蝶数据失败，参数：{},失败信息：{}", JsonUtils.toJson(connectorParam), msg);
            throw new FinhubException(-9999, "同步金蝶数据失败,失败信息：" + msg);
        }
    }
}
