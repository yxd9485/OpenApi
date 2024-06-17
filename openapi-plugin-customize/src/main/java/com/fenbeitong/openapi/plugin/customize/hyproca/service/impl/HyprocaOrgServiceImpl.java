package com.fenbeitong.openapi.plugin.customize.hyproca.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.impl.PrimaryOrganizationServiceImpl;
import com.fenbeitong.openapi.plugin.customize.hyproca.service.HyprocaOrgService;
import com.fenbeitong.openapi.plugin.support.employee.service.SupportEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.organization.service.SupportFunDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: TalkOrganizationService</p>
 * <p>Description: 海普诺凯组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
@ServiceAspect
@Service
public class HyprocaOrgServiceImpl  implements HyprocaOrgService {

    @Autowired
    UserCenterService userCenterService;

    @Autowired
    SupportEmployeeService supportEmployeeService;

    @Autowired
    SupportFunDepartmentService supportFunDepartmentService;

    @Autowired
    OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    PrimaryOrganizationServiceImpl primaryOrganizationService;

    /**
     * 全量同步
     */
    @Override
    public String allSync(String companyId, String topId) {
        return primaryOrganizationService.allSync(companyId, topId);
    }

    /**
     * 增量同步 (作废)
     */
    @Override
    public String syncOrganizationProtion(String companyId) {

        // 增量部门数据
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList = primaryOrganizationService.syncDepPortion(companyId);
        // 增量人员数据
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList = primaryOrganizationService.syncEmpPortion(companyId);
        // 有效部门
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOListTrue = new ArrayList<>();
        // 无效部门
        List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOListFalse = new ArrayList<>();
        // 有效人员
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOListTrue = new ArrayList<>();
        // 无效人员
        List<OpenThirdEmployeeDTO> openThirdEmployeeDTOListFalse = new ArrayList<>();

        if (!ObjectUtils.isEmpty(openThirdOrgUnitDTOList)) {
            openThirdOrgUnitDTOListTrue = openThirdOrgUnitDTOList.stream().filter(t -> !"1".equals(t.getIsStop())).collect(Collectors.toList());
            openThirdOrgUnitDTOListFalse = openThirdOrgUnitDTOList.stream().filter(t -> "1".equals(t.getIsStop())).collect(Collectors.toList());
            // 新增增量部门
            supportFunDepartmentService.syncWithThirdTopId(openThirdOrgUnitDTOListTrue);
            // 删除无效部门
            supportFunDepartmentService.deleteBatch(openThirdOrgUnitDTOListFalse, companyId);
        }

        if (!ObjectUtils.isEmpty(openThirdEmployeeDTOList)) {
            openThirdEmployeeDTOListTrue = openThirdEmployeeDTOList.stream().filter(t -> !"1".equals(t.getIsStop())).collect(Collectors.toList());
            openThirdEmployeeDTOListFalse = openThirdEmployeeDTOList.stream().filter(t -> "1".equals(t.getIsStop())).collect(Collectors.toList());
            // 删除无效人员
            supportEmployeeService.deleteBatch(openThirdEmployeeDTOListFalse, companyId);
            // 新增增量人员
            supportEmployeeService.syncWithThirdTopId(openThirdEmployeeDTOListTrue);

        }

        // 设置部门负责人
        if (!ObjectUtils.isEmpty(openThirdOrgUnitDTOListTrue)) {
            // 保存部门负责人到中间表
            openThirdOrgUnitDao.updataDapartmentByMasterIds(openThirdOrgUnitDTOListTrue, companyId, OpenType.DINGTALK_EIA.getType());
            // 设置部门主管
            List<String> thirdOrgUnitIds = openThirdOrgUnitDTOListTrue.stream().map(OpenThirdOrgUnitDTO::getThirdOrgUnitId).collect(Collectors.toList());
            List<OpenThirdOrgUnit> openThirdOrgUnits = openThirdOrgUnitDao.listOrgUnitByMaster(OpenType.OPEN_API.getType(), companyId, thirdOrgUnitIds);
            openSyncThirdOrgService.setDepManage(openThirdOrgUnits, companyId, OpenType.OPEN_API.getType());

        }

        return "success";
    }


}
