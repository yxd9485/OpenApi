package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonClientDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

@ServiceAspect
@Service
public class SeeyonClientService {
    @Autowired
    SeeyonClientDao seeyonClientDao;

    public SeeyonClient getSeeyonClientByName(String seeyonOrgName) {
        Example example = new Example(SeeyonClient.class);
        example.createCriteria().andEqualTo("seeyonOrgName",seeyonOrgName);
        SeeyonClient byExample = seeyonClientDao.getByExample(example);
        return byExample;
    }

    public SeeyonClient getSeeyonClientByCompanyId(String companyId) {
        Example example = new Example(SeeyonClient.class);
        example.createCriteria().andEqualTo("openapiAppId",companyId);
        SeeyonClient byExample = seeyonClientDao.getByExample(example);
        return byExample;
    }
}
