package com.fenbeitong.openapi.plugin.func.employee.service;

import com.fenbeitong.openapi.plugin.core.constant.LogConstant;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.company.dao.PhoneValidateDao;
import com.fenbeitong.openapi.plugin.support.employee.dto.*;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.employee.service.IEmployeeRankTemplateService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

/**
 * 用户访问功能集成实现
 * Created by log.chang on 2019/12/3.
 */
@ServiceAspect
@Service
@Slf4j
public class FuncEmployeeService extends AbstractEmployeeService {

    @Autowired
    private CommonAuthService signService;

    @Autowired
    private PhoneValidateDao phoneValidateDao;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private IEmployeeRankTemplateService employeeRankTemplateService;

    @Value("${host.usercenter}")
    private String ucHost;

    public Object createUser(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportCreateEmployeeReqDTO supportCreateEmployeeReqDTO = JsonUtils.toObj(request.getData(), SupportCreateEmployeeReqDTO.class);
        if (supportCreateEmployeeReqDTO == null) {
            throw new OpenApiFuncException(FuncResponseCode.OPANAPI_SYNC_ORG_EMPLOYEE_ERROR, "参数错误");
        }
        //校验身份信息，与openapi2.0校验一致
        if (CollectionUtils.isNotBlank(supportCreateEmployeeReqDTO.getEmployeeList())) {
            for (SupportEmployeeInsertDTO emp : supportCreateEmployeeReqDTO.getEmployeeList()) {
                ValidatorUtils.validateBySpring(emp);
                emp.setCertInfo(assembleCertInfo(emp.getCertName(), emp.getCertLastName(), emp.getCertFirstName(), emp.getCertList()));
            }
        }
        boolean useRank = employeeRankTemplateService.useRank(appId);
        //补全supportCreateEmployeeReqDTO的companyId
        supportCreateEmployeeReqDTO.setCompanyId(appId);
        supportCreateEmployeeReqDTO.setOperatorId(superAdmin(appId));
        List<SupportEmployeeInsertDTO> employeeList = supportCreateEmployeeReqDTO.getEmployeeList();
        if (!ObjectUtils.isEmpty(employeeList)) {
            List<OpenMsgSetup> virtualPhone = openMsgSetupDao.listByCompanyIdAndItemCodeList(appId, Lists.newArrayList("virtual_phone"));
            for (SupportEmployeeInsertDTO supportEmployeeInsertDTO : employeeList) {
                //补全employeeList的companyId
                supportEmployeeInsertDTO.setCompanyId(appId);
                //处理虚拟手机号
                if (!ObjectUtils.isEmpty(virtualPhone) && StringUtils.isBlank(supportEmployeeInsertDTO.getPhone())) {
                    supportEmployeeInsertDTO.setPhone(StringUtils.obj2str(virtualPhoneUtils.getVirtualPhone(appId, supportEmployeeInsertDTO.getThirdEmployeeId())));
                }
                String roleType = supportEmployeeInsertDTO.getRoleType();
                if (!ObjectUtils.isEmpty(roleType)) {
                    if (useRank) {
                        supportEmployeeInsertDTO.setTemplateName(roleType);
                    }
                }
            }
        }
        OpenApiRespDTO openApiRespDTO = super.createUserForAPI(supportCreateEmployeeReqDTO);
        openApiRespDTO.setRequestId(MDC.get(LogConstant.MDC_KEY_REQUESTID));
        return openApiRespDTO;
    }

    public Object updateUser(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportUpdateEmployeeReqDTO supportUpdateEmployeeReqDTO = JsonUtils.toObj(request.getData(), SupportUpdateEmployeeReqDTO.class);
        if (supportUpdateEmployeeReqDTO == null) {
            throw new OpenApiFuncException(FuncResponseCode.OPANAPI_SYNC_ORG_EMPLOYEE_ERROR, "参数错误");
        }
        //校验身份信息，与openapi2.0校验一致
        if (CollectionUtils.isNotBlank(supportUpdateEmployeeReqDTO.getEmployeeList())) {
            for (SupportEmployeeUpdateDTO emp : supportUpdateEmployeeReqDTO.getEmployeeList()) {
                ValidatorUtils.validateBySpring(emp);
                emp.setCertInfo(assembleCertInfo(emp.getCertName(), emp.getCertLastName(), emp.getCertFirstName(), emp.getCertList()));
            }
        }
        supportUpdateEmployeeReqDTO.setCompanyId(appId);
        supportUpdateEmployeeReqDTO.setOperatorId(superAdmin(appId));
        boolean useRank = employeeRankTemplateService.useRank(appId);
        List<SupportEmployeeUpdateDTO> employeeList = supportUpdateEmployeeReqDTO.getEmployeeList();
        if (!ObjectUtils.isEmpty(employeeList)) {
            for (SupportEmployeeUpdateDTO supportEmployeeUpdateDTO : employeeList) {
                String roleType = supportEmployeeUpdateDTO.getRoleType();
                if (!ObjectUtils.isEmpty(roleType)) {
                    if (useRank) {
                        supportEmployeeUpdateDTO.setTemplateName(roleType);
                    }
                }
            }
        }
        OpenApiRespDTO openApiRespDTO = super.updateUserForAPI(supportUpdateEmployeeReqDTO);
        openApiRespDTO.setRequestId(MDC.get(LogConstant.MDC_KEY_REQUESTID));
        return openApiRespDTO;
    }

    public Object deleteUser(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportDeleteEmployeeReqDTO supportDeleteEmployeeReqDTO = JsonUtils.toObj(request.getData(), SupportDeleteEmployeeReqDTO.class);
        if (supportDeleteEmployeeReqDTO == null) {
            throw new OpenApiFuncException(FuncResponseCode.OPANAPI_SYNC_ORG_EMPLOYEE_ERROR, "参数错误");
        }
        supportDeleteEmployeeReqDTO.setCompanyId(appId);
        supportDeleteEmployeeReqDTO.setOperatorId(superAdmin(appId));
        OpenApiRespDTO openApiRespDTO = super.deleteUserForAPI(supportDeleteEmployeeReqDTO);
        openApiRespDTO.setRequestId(MDC.get(LogConstant.MDC_KEY_REQUESTID));
        return openApiRespDTO;
    }

    public Object bindUser(ApiRequest request) throws Exception {
        signService.checkSign(request);
        String appId = signService.getAppId(request);
        SupportBindEmployeeReqDTO supportBindEmployeeReqDTO = JsonUtils.toObj(request.getData(), SupportBindEmployeeReqDTO.class);
        if (supportBindEmployeeReqDTO == null) {
            throw new OpenApiFuncException(FuncResponseCode.OPANAPI_SYNC_ORG_EMPLOYEE_ERROR, "参数错误");
        }
        supportBindEmployeeReqDTO.setCompanyId(appId);
        supportBindEmployeeReqDTO.setOperatorId(superAdmin(appId));
        OpenApiRespDTO openApiRespDTO = super.bindUserForAPI(supportBindEmployeeReqDTO);
        openApiRespDTO.setRequestId(MDC.get(LogConstant.MDC_KEY_REQUESTID));
        return openApiRespDTO;
    }

    /**
     * 组装身份信息实体
     * @param certName      证件姓名
     * @param certLastName  证件英文姓
     * @param certFirstName 证件英文名
     * @param certs         证件列表
     * @return  组装好的certInfo
     */
    private CertInfoReqDTO assembleCertInfo(String certName, String certLastName, String certFirstName, List<CertDTO> certs) {
        if (!StringUtils.isTrimBlank(certName)
            || !StringUtils.isTrimBlank(certLastName)
            || !StringUtils.isTrimBlank(certFirstName)) {
            return CertInfoReqDTO.builder()
                .name(certName)
                .givenName(Objects.isNull(certFirstName) ? certFirstName : certFirstName.toUpperCase())
                .familyName(Objects.isNull(certLastName) ? certLastName : certLastName.toUpperCase())
                .build();
        }
        return null;
    }

}
