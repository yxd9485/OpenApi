package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.uc;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaFbEmployeeService {
    @DubboReference(check = false)
    IThirdEmployeeService iThirdEmployeeService;
    @DubboReference(check = false)
    IAuthService iAuthService;

    public String getCreateTripApproveToken(Object... createApproveParams) {
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId((String) createApproveParams[0]);
        thirdEmployeeContract.setEmployeeId((String) createApproveParams[1]);
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setUserType(2);
        //调用uc接口根据公司ID和人员ID获取手机号
        try {
            ThirdEmployeeRes thirdEmployeeRes = iThirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
            if (!ObjectUtils.isEmpty(thirdEmployeeRes)) {
                String phoneNum = thirdEmployeeRes.getEmployee().getPhone_num();
                if (StringUtils.isNotBlank(phoneNum)) {
                    //调用uc鉴权获取个人token
                    LoginResVO loginResVO = iAuthService.loginAuthInitV5((String) createApproveParams[0], (String) createApproveParams[1], phoneNum, IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.OPENAPI.getPlatform(), CompanyLoginChannelEnum.OPENAPI.getEntrance());
                    String token = loginResVO.getLogin_info().getToken();
                    return token;
                }
            }
        } catch (Exception e) {
            log.info("人员不在分贝通组织架构内 {}", createApproveParams[1]);
        }

        return null;
    }

    /**
     * 根据人员三方ID查询分贝人员信息
     * @param createApproveParams
     * @return
     */
    public ThirdEmployeeRes getFbEmployeeInfo(Object... createApproveParams) {
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId((String) createApproveParams[0]);
        thirdEmployeeContract.setEmployeeId((String) createApproveParams[1]);
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setUserType(2);
        //调用uc接口根据公司ID和人员ID获取手机号
        ThirdEmployeeRes thirdEmployeeRes = null;
        try {
            thirdEmployeeRes = iThirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
        } catch (Exception e) {
            log.info("人员不在分贝通组织架构内 {}", createApproveParams[1]);
            return null;
        }
        return thirdEmployeeRes;
    }


}
