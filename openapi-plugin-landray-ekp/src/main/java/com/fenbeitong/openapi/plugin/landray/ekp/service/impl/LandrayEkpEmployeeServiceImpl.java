package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.landray.ekp.common.LandaryEkpConstant;
import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpEmployeeDTO;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.ILandrayEkpEmployeeService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
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
public class LandrayEkpEmployeeServiceImpl implements ILandrayEkpEmployeeService {

    @Override
    public List<LandaryEkpEmployeeDTO> getAllEmployee(OpenLandrayEkpConfig openLandrayEkpConfig, String beginTime) {
        ISysSynchroGetOrgWebServiceServiceLocator locator = new ISysSynchroGetOrgWebServiceServiceLocator(openLandrayEkpConfig.getWsUrl(), openLandrayEkpConfig.getUserName(), openLandrayEkpConfig.getPassword());
        ISysSynchroGetOrgWebService httpPort = null;
        List<LandaryEkpEmployeeDTO> employeeList = Lists.newArrayList();
        try {
            httpPort = locator.getISysSynchroGetOrgWebServicePort();
            log.info("get employee from landray ekp ,companyId={},beginTime={}", openLandrayEkpConfig.getCompanyId(), beginTime);
            SysSynchroGetOrgInfoContext context = new SysSynchroGetOrgInfoContext();
            context.setCount(100000);
            context.setReturnOrgType("[{\"type\":\"person\"}]");
            context.setBeginTimeStamp(beginTime);
            SysSynchroOrgResult elementsBaseInfo = httpPort.getUpdatedElements(context);
            log.info("get employee from landray ekp companyId={}, res={}", openLandrayEkpConfig.getCompanyId(), elementsBaseInfo.getMessage());
            if (elementsBaseInfo.getReturnState() == LandaryEkpConstant.EKP_STATUS_SUCCESS) {
                employeeList = JsonUtils.toObj(elementsBaseInfo.getMessage(), new TypeReference<List<LandaryEkpEmployeeDTO>>() {
                });
            }
        } catch (ServiceException | RemoteException e) {
            log.warn("调用蓝凌异常：", e);
        }
        return employeeList;
    }


    @Override
    public List<LandaryEkpEmployeeDTO> getAvailableEmployee(List<LandaryEkpEmployeeDTO> employeeList) {
        List<LandaryEkpEmployeeDTO> availableEmployee = employeeList.stream().filter(e -> e.getIsAvailable() != null && e.getIsAvailable()).collect(Collectors.toList());
        return availableEmployee;
    }

    @Override
    public List<LandaryEkpEmployeeDTO> getUnAvailableEmployee(List<LandaryEkpEmployeeDTO> employeeList) {
        List<LandaryEkpEmployeeDTO> unAvailableEmployee = employeeList.stream().filter(e -> e.getIsAvailable() != null && !e.getIsAvailable()).collect(Collectors.toList());
        return unAvailableEmployee;
    }
}
