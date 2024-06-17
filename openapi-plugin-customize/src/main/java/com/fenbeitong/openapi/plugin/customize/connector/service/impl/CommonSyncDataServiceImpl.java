package com.fenbeitong.openapi.plugin.customize.connector.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.connector.service.CommonSyncDataService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @ClassName ConnectorSyncDataServiceImpl
 * @Description 使用连接器同步数据
 * @Company www.fenbeitong.com
 * @Author chengzhigang1
 * @Date 2022/9/16 下午17:50
 **/
@Slf4j
@Service
public class CommonSyncDataServiceImpl implements CommonSyncDataService {

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public void connectorSyncData(String companyId, String settingCode) {
        //从open_msg_setup表中取出连接器配置
        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, settingCode);
        if (ObjectUtils.isEmpty(openMsgSetup) || StringUtils.isEmpty(openMsgSetup.getStrVal1())) {
            log.info("数据同步连接器配置为空，请检查，公司id:{},itemCode:{}", companyId, settingCode);
            throw new OpenApiArgumentException("找不到同步连接器配置信息");
        }
        RestHttpUtils.postJson(openMsgSetup.getStrVal1(), null, openMsgSetup.getStrVal2());
    }


}
