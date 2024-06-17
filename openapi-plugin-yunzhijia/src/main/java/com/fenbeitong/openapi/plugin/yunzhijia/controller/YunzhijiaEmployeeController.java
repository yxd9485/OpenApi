package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaEmployeeService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/yunzhijia/employee")
public class YunzhijiaEmployeeController {

    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    IYunzhijiaEmployeeService yunzhijiaEmployeeServiceImpl;
    @DubboReference(check = false)
    IThirdEmployeeService iThirdEmployeeService;
    @DubboReference(check = false)
    IAuthService iAuthService;

    @RequestMapping("/detail")
    @ResponseBody
    public Object getYunzhijiaEmployeeDetail(@RequestBody YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO) {
        YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> yunzhijiaEmployeeDetail = yunzhijiaEmployeeServiceImpl.getYunzhijiaEmployeeDetail(yunzhijiaEmployeeReqDTO);
        return YunzhijiaResponseUtils.success(yunzhijiaEmployeeDetail.getData());
    }

    @RequestMapping("/test/getFbToken")
    @ResponseBody
    public Object getYunzhijiaFbEmployeeToken(@RequestBody YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO) {
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId("5cc1598623445f612c73b27a");
        thirdEmployeeContract.setEmployeeId("5e688014e4b0e976d875ffb6");
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setUserType(2);
        //调用uc接口根据公司ID和人员ID获取手机号
        ThirdEmployeeRes thirdEmployeeRes = iThirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
        if(!ObjectUtils.isEmpty(thirdEmployeeRes)){
            String phoneNum = thirdEmployeeRes.getEmployee().getPhone_num();
            if(StringUtils.isNotBlank(phoneNum)){
                //调用uc鉴权获取个人token
                LoginResVO loginResVO = iAuthService.loginAuthInitV5("5cc1598623445f612c73b27a", "5e688014e4b0e976d875ffb6", phoneNum, IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.OPENAPI.getPlatform(), CompanyLoginChannelEnum.OPENAPI.getEntrance());
                String token = loginResVO.getLogin_info().getToken();
                return token;
            }
        }
        return null;
    }

    /**
     * 获取云之家全量数据
     * @param yunzhijiaEmployeeReqDTO
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object getYunzhijiaEmployeeList(@RequestBody YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO) {
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(yunzhijiaEmployeeReqDTO.getEid());
        if (ObjectUtils.isEmpty(yunzhijiaToken)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        //2.获取access_token对象构建
        YunzhijiaAccessTokenReqDTO yunzhijiaAccessTokenReqDTO = YunzhijiaAccessTokenReqDTO.builder()
                .eid(yunzhijiaEmployeeReqDTO.getEid())
                .secret(yunzhijiaToken.getCorpSecret())
                .scope(YunzhijiaResourceLevelConstant.RES_GROUP_SECRET)
                .timestamp(System.currentTimeMillis())
                .build();
        YunzhijiaResponse<YunzhijiaAccessTokenRespDTO> yunzhijiaAccessToken = yunzhijiaTokenService.getYunzhijiaRemoteAccessToken(yunzhijiaAccessTokenReqDTO);
        if (yunzhijiaAccessToken.getErrorCode() != RespCode.SUCCESS) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_ERROR)));
        }
        //2.获取返回的云之家access_token
        String accessToken = yunzhijiaAccessToken.getData().getAccessToken();
        List<YunzhijiaEmployeeDTO> yunzhijiaEmployeeDetail = yunzhijiaEmployeeServiceImpl.getYunzhijiaEmployeeList(accessToken,yunzhijiaEmployeeReqDTO);
        return YunzhijiaResponseUtils.success(yunzhijiaEmployeeDetail);
    }

}
