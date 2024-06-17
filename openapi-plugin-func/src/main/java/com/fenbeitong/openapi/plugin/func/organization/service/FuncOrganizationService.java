package com.fenbeitong.openapi.plugin.func.organization.service;

import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportBindOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportCreateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportDeleteOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportUpdateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 组织架构功能集成实现
 * Created by log.chang on 2019/12/3.
 */
@ServiceAspect
@Service
public class FuncOrganizationService extends AbstractOrganizationService {

    @Autowired
    private CommonAuthService signService;

    public Object createDepartment(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportCreateOrgUnitReqDTO supportCreateOrgUnitReqDTO = JsonUtils.toObj(request.getData(), SupportCreateOrgUnitReqDTO.class);
        supportCreateOrgUnitReqDTO.setCompanyId(appId);
        supportCreateOrgUnitReqDTO.setOperatorId(superAdmin(appId));
        OpenApiRespDTO openApiRespDTO = super.createDepartmentForAPI(appId, supportCreateOrgUnitReqDTO);
        return openApiRespDTO;
    }

    public Object updateDepartment(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportUpdateOrgUnitReqDTO supportUpdateOrgUnitReqDTO = JsonUtils.toObj(request.getData(), SupportUpdateOrgUnitReqDTO.class);
        supportUpdateOrgUnitReqDTO.setCompanyId(appId);
        supportUpdateOrgUnitReqDTO.setOperatorId(superAdmin(appId));
        OpenApiRespDTO openApiRespDTO = super.updateDepartmentForAPI(appId, supportUpdateOrgUnitReqDTO);
        return openApiRespDTO;
    }

    public Object deleteDepartment(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportDeleteOrgUnitReqDTO supportDeleteOrgUnitReqDTO = JsonUtils.toObj(request.getData(), SupportDeleteOrgUnitReqDTO.class);
        supportDeleteOrgUnitReqDTO.setCompanyId(appId);
        supportDeleteOrgUnitReqDTO.setOperatorId(superAdmin(appId));
        OpenApiRespDTO openApiRespDTO = super.deleteDepartmentForAPI(supportDeleteOrgUnitReqDTO);
        return openApiRespDTO;
    }

    public Object bindDepartment(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportBindOrgUnitReqDTO supportBindOrgUnitReqDTO = JsonUtils.toObj(request.getData(), SupportBindOrgUnitReqDTO.class);
        supportBindOrgUnitReqDTO.setCompanyId(appId);
        OpenApiRespDTO openApiRespDTO = super.bindDepartmentForAPI(supportBindOrgUnitReqDTO);
        return openApiRespDTO;
    }


}
