package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonFbErrorOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbErrorOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.service.FbErrorOrgEmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
public class FbErrorOrgEmpServiceImpl implements FbErrorOrgEmpService {
@Autowired
    SeeyonFbErrorOrgEmpDao seeyonFbErrorOrgEmpDao;

    @Override
    public int saveSeeyonFbErrorOrgEmp(SeeyonFbErrorOrgEmp seeyonFbErrorOrgEmp) {
        return seeyonFbErrorOrgEmpDao.save(seeyonFbErrorOrgEmp);
    }
}
