package com.fenbeitong.openapi.plugin.ecology.v8.standard.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyAuthService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 泛微授权接口
 * @Auther zhang.peng
 * @Date 2022/1/11
 */
@RestController
@RequestMapping("/ecology/standard/auth")
@Slf4j
public class EcologyAuthController {

    @Autowired
    private IEcologyAuthService ecologyAuthService;

    @RequestMapping("/getUserInfo")
    public Object getUserInfo( @RequestParam("companyId") String companyId, @RequestParam("phone") String phone){
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(phone)){
            log.info("公司id或者手机号为空");
            return OpenapiResponseUtils.error(RespCode.ARGUMENT_ERROR,"手机号或者公司id为空");
        }
        LoginResVO loginResVO = ecologyAuthService.getLoginInfo(companyId,phone);
        return OpenapiResponseUtils.success(loginResVO);
    }
}
