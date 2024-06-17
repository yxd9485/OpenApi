package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.impl.PrimaryOrganizationServiceImpl;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.NingBoWeiLiOrgService;
import com.fenbeitong.openapi.plugin.support.employee.service.SupportEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.organization.service.SupportFunDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: TalkOrganizationService</p>
 * <p>Description: 宁波伟立组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
@ServiceAspect
@Service
public class NingBoWeiLiOrgServiceImpl implements NingBoWeiLiOrgService {

    @Autowired
    UserCenterService userCenterService;

    @Autowired
    SupportEmployeeService supportEmployeeService;

    @Autowired
    SupportFunDepartmentService supportFunDepartmentService;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    PrimaryOrganizationServiceImpl primaryOrganizationService;


    /**
     * 全量同步
     */
    @Override
    public String allSync(String companyId, String topId) {
        return primaryOrganizationService.allSync(companyId, topId);
    }


}
