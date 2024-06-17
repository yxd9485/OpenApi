package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 员工信息查询
 * @Auther zhang.peng
 * @Date 2021/8/16
 */
@ServiceAspect
@Service
@Slf4j
public class LandrayEmployeeServiceImpl {

    @Autowired
    private BaseEmployeeRefServiceImpl baseEmployeeRefService;

    public ThirdEmployeeRes getEmployeeFbInfo(String companyId , String emplyeeId) {
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId(companyId);
        thirdEmployeeContract.setEmployeeId(emplyeeId);
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setUserType(2);
        //调用uc接口根据公司ID和人员ID获取手机号
        ThirdEmployeeRes thirdEmployeeRes = null;
        try {
            IThirdEmployeeService thirdEmployeeService = baseEmployeeRefService.getThirdEmployeeService();
            thirdEmployeeRes = thirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
        } catch (Exception e) {
            log.info("人员不在分贝通组织架构内 , companyId {}", companyId);
            return null;
        }
        return thirdEmployeeRes;
    }

    public ThirdEmployeeRes getEmployeeFbPhoneInfo(String companyId , String employeeId , String thirdEmployeeId) {
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId(companyId);
        if (StringUtils.isBlank(thirdEmployeeId)){
            thirdEmployeeContract.setEmployeeId(employeeId);
            thirdEmployeeContract.setType(IdTypeEnums.FB_ID.getKey());
            thirdEmployeeContract.setUserType(IdTypeEnums.FB_ID.getKey());
        } else {
            thirdEmployeeContract.setEmployeeId(thirdEmployeeId);
            thirdEmployeeContract.setType(IdTypeEnums.FB_ID.getKey());
            thirdEmployeeContract.setUserType(IdTypeEnums.THIRD_ID.getKey());
        }
        //调用uc接口根据公司ID和人员ID获取手机号
        ThirdEmployeeRes thirdEmployeeRes = null;
        try {
            IThirdEmployeeService thirdEmployeeService = baseEmployeeRefService.getThirdEmployeeService();
            thirdEmployeeRes = thirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
        } catch (Exception e) {
            log.info("人员不在分贝通组织架构内 , companyId {}", companyId);
            return null;
        }
        return thirdEmployeeRes;
    }
}
