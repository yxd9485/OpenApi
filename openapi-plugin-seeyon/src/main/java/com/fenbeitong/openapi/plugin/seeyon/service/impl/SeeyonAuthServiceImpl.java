package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.service.ISeeyonAuthService;
import com.fenbeitong.openapi.plugin.seeyon.utils.AESUtils;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcFetchEmployInfoReqDto;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 致远授权接口
 * @Auther xiaohai
 * @Date 2022/09/27
 */
@ServiceAspect
@Service
@Slf4j
public class SeeyonAuthServiceImpl implements ISeeyonAuthService {

    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Override
    public LoginResVO getLoginInfo( String authCode ){
        String decrypt = AESUtils.decrypt(authCode, SeeyonConstant.ENCRPT_KEY);
        log.info("解密后数据：{}" , decrypt);
        UcFetchEmployInfoReqDto ucFetchEmployInfoReqDto = JsonUtils.toObj(decrypt, UcFetchEmployInfoReqDto.class);
        return openEmployeeService.fetchLoginAuthInfoByPhoneNum(ucFetchEmployInfoReqDto, CompanyLoginChannelEnum.PANWEIBO_H5);
    }
}
