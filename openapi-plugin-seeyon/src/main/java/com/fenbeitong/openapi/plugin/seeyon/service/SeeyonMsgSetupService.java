package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
public class SeeyonMsgSetupService {
    @Autowired
    SeeyonOpenMsgSetupDao seeyonOpenMsgSetupDao;

    /**
     * 根据条件获取公司配置
     * @param map
     * @return
     */
    public SeeyonOpenMsgSetup getSeeyonCompanySetup(Map<String, Object> map) {
        List<SeeyonOpenMsgSetup> list = seeyonOpenMsgSetupDao.listOpenMsgSetup(map);
        if (!ObjectUtils.isEmpty(list)) {
            SeeyonOpenMsgSetup seeyonOpenMsgSetup = list.get(0);
            return seeyonOpenMsgSetup;
        }
        return null;

    }


}
