package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.employee;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.YunzhijiaUserReqDto;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaEmployeeService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaEmployeeServiceImpl extends AbstractEmployeeService implements IYunzhijiaEmployeeService {


    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    YunzhijiaRemoteEmployeeService yunzhijiaRemoteEmployeeService;
    @Autowired
    private BaseEmployeeRefServiceImpl baseEmployeeRefService;

    /**
     * 根据员工信息查询员工详情
     *
     * @param yunzhijiaEmployeeReqDTO
     * @return
     */
    public YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> getYunzhijiaEmployeeDetail(YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO) {
        //1.根据企业ID查询通讯录token
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(yunzhijiaEmployeeReqDTO.getEid());
        if (ObjectUtils.isEmpty(yunzhijiaToken)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        //2.获取access_token对象构建
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(yunzhijiaEmployeeReqDTO.getEid())
                .secret(yunzhijiaToken.getCorpSecret())
                .scope(YunzhijiaResourceLevelConstant.RES_GROUP_SECRET)
                .timestamp(System.currentTimeMillis())
                .build();
        //3.获取云之家源数据
        YunzhijiaResponse<List<YunzhijiaEmployeeDTO>> yunzhijiaRemoteEmployeeDetail = yunzhijiaRemoteEmployeeService.getYunzhijiaRemoteEmployeeDetail(build, yunzhijiaEmployeeReqDTO);
        return yunzhijiaRemoteEmployeeDetail;
    }

    @Override
    public List<YunzhijiaEmployeeDTO> getYunzhijiaEmployeeList(String accessToken, YunzhijiaEmployeeReqDTO yunzhijiaEmployeeReqDTO) {
        //1.根据企业ID查询通讯录token
        //2.获取access_token对象构建
        YunzhijiaAllEmployeeReqDTO yunzhijiaAllEmployeeReqDTO = YunzhijiaAllEmployeeReqDTO.builder()
                .eid(yunzhijiaEmployeeReqDTO.getEid())
                .build();
        //3.获取云之家全部人员源数据
        List<YunzhijiaEmployeeDTO> yunzhijiaRemoteEmployeeList = yunzhijiaRemoteEmployeeService.getYunzhijiaRemoteEmployeeList(accessToken, yunzhijiaAllEmployeeReqDTO);
        return yunzhijiaRemoteEmployeeList;

    }

}
