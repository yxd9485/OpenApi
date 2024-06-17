package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenSyncThirdOrgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

@ServiceAspect
@Service
public class SeeyonSyncThirdOrgService extends OpenSyncThirdOrgServiceImpl {

    @Autowired
    AuthDefinitionDao authDefinitionDao;
    @Autowired
    SeeyonSyncEmployeeService seeyonSyncEmployeeService;


    public AuthDefinition getAuthDefinitionByCompanyId(String companyId) {
        AuthDefinition authInfoByAppId = authDefinitionDao.getAuthInfoByAppId(companyId);
        return authInfoByAppId;
    }

    @Override
    protected SeeyonSyncEmployeeService getOpenEmployeeService() {
        return SpringUtils.getBean(SeeyonSyncEmployeeService.class);
    }

}
