package com.fenbeitong.openapi.plugin.customize.sync.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.impl.PrimaryOrganizationServiceImpl;
import com.fenbeitong.openapi.plugin.customize.common.service.impl.PrimaryProjectServiceImpl;
import com.fenbeitong.openapi.plugin.customize.sync.service.OrgAndProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
public class OrgAndProjectServiceImpl implements OrgAndProjectService {

    @Autowired
    PrimaryOrganizationServiceImpl primaryOrganizationService;

    @Autowired
    PrimaryProjectServiceImpl primaryProjectService;


    @Override
    public String OrgSync(String companyId, String topId) {
        return primaryOrganizationService.allSync(companyId, topId);
    }

    @Override
    public String ProjectSync(String companyId, Integer type,boolean constraintUpdate) {
        return primaryProjectService.syncProject(companyId, type,constraintUpdate);
    }
}
