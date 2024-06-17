package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.landray.ekp.common.LandaryEkpConstant;
import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpDepartmentInfoDTO;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.ILandrayEkpOrganizationService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.landray.ekp.org.ISysSynchroGetOrgWebService;
import com.landray.ekp.org.ISysSynchroGetOrgWebServiceServiceLocator;
import com.landray.ekp.org.SysSynchroGetOrgInfoContext;
import com.landray.ekp.org.SysSynchroOrgResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 蓝凌EKP组织机构
 *
 * @author lizhen
 * @date 2021/1/27
 */
@ServiceAspect
@Service
@Slf4j
public class LandrayEkpOrganizationServiceImpl implements ILandrayEkpOrganizationService {

    @Override
    public List<LandaryEkpDepartmentInfoDTO> getAllDepartment(OpenLandrayEkpConfig openLandrayEkpConfig, String beginTime) {
        ISysSynchroGetOrgWebServiceServiceLocator locator = new ISysSynchroGetOrgWebServiceServiceLocator(openLandrayEkpConfig.getWsUrl(), openLandrayEkpConfig.getUserName(), openLandrayEkpConfig.getPassword());
        ISysSynchroGetOrgWebService httpPort = null;
        List<LandaryEkpDepartmentInfoDTO> departmentList = Lists.newArrayList();
        try {
            httpPort = locator.getISysSynchroGetOrgWebServicePort();
            log.info("get department from landray ekp,companyId={},beginTime={}", openLandrayEkpConfig.getCompanyId(), beginTime);
            SysSynchroGetOrgInfoContext context = new SysSynchroGetOrgInfoContext();
            context.setCount(100000);
            context.setReturnOrgType("[{\"type\":\"dept\"}]");
            context.setBeginTimeStamp(beginTime);
            SysSynchroOrgResult elementsBaseInfo = httpPort.getUpdatedElements(context);
            log.info("get department from landray ekp companyId={}, res={}", openLandrayEkpConfig.getCompanyId(), elementsBaseInfo.getMessage());
            if (elementsBaseInfo.getReturnState() == LandaryEkpConstant.EKP_STATUS_SUCCESS) {
                departmentList = JsonUtils.toObj(elementsBaseInfo.getMessage(), new TypeReference<List<LandaryEkpDepartmentInfoDTO>>() {
                });
            }
        } catch (ServiceException | RemoteException e) {
            log.warn("调用蓝凌异常：", e);
        }
        return departmentList;
    }


    @Override
    public List<LandaryEkpDepartmentInfoDTO> getAvailableDepartment(List<LandaryEkpDepartmentInfoDTO> departmentList, String companyId) {
        List<LandaryEkpDepartmentInfoDTO> availableDepartment = departmentList.stream().filter(d -> d.getIsAvailable()).map(d -> {
            if (StringUtils.isBlank(d.getParent())) {
                d.setParent(companyId);
            }
            return d;
        }).collect(Collectors.toList());
        return availableDepartment;
    }

    @Override
    public List<LandaryEkpDepartmentInfoDTO> getUnAvailableDepartment(List<LandaryEkpDepartmentInfoDTO> departmentList) {
        List<LandaryEkpDepartmentInfoDTO> unAvailableDepartment = departmentList.stream().filter(d -> !d.getIsAvailable()).collect(Collectors.toList());
        return unAvailableDepartment;
    }
}
