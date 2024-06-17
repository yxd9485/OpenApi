package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyAuthService;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcFetchEmployInfoReqDto;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 泛微授权接口
 * @Auther zhang.peng
 * @Date 2022/1/11
 */
@ServiceAspect
@Service
@Slf4j
public class EcologyAuthServiceImpl implements IEcologyAuthService {

    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Override
    public LoginResVO getLoginInfo( String companyId , String phone ){
        UcFetchEmployInfoReqDto ucFetchEmployInfoReqDto = new UcFetchEmployInfoReqDto();
        ucFetchEmployInfoReqDto.setCompanyId(companyId);
        ucFetchEmployInfoReqDto.setPhone(phone);
        return openEmployeeService.fetchLoginAuthInfoByPhoneNum(ucFetchEmployInfoReqDto, CompanyLoginChannelEnum.PANWEIBO_H5);
    }
}
