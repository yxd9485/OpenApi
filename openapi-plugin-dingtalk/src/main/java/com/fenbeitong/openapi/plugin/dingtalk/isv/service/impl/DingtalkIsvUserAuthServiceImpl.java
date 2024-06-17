package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.dingtalk.api.request.OapiSsoGetuserinfoRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiSsoGetuserinfoResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvWebLoginInfoRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvAuthRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvUserAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdOrgUnitDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdOrgUnit;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmpowerEmployeeRoleDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.model.dto.menu.MenuInfoVo;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.model.enums.employee.EmployeeRole;
import com.fenbeitong.usercenter.api.model.enums.menu.MenuType;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.fenbeitong.usercenter.api.service.menu.IMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/7/20
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvUserAuthServiceImpl implements IDingtalkIsvUserAuthService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;


    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    @Autowired
    private OpenThirdOrgUnitDao openThirdOrgUnitDao;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @DubboReference(check = false)
    private IAuthService iAuthService;

    @DubboReference(check = false)
    private IMenuService iMenuService;

    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;

    @Override
    public DingtalkIsvAuthRespDTO appAuth(String code, String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = getCompanyByCorpId(corpId);
        //判断是否是个人
        String mainCorpId = dingtalkIsvCompany.getMainCorpId();
        if(StringUtils.isBlank(mainCorpId)){
            //企业
            String userId = getUserInfo(code, corpId);
            String companyId = dingtalkIsvCompany.getCompanyId();
            return DingtalkIsvAuthRespDTO.builder().companyId(companyId).thirdEmployeeId(userId).type("2").corpId(mainCorpId).build();
        }
        String openSysConfigByCode = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.DINGTAlK_ISV_COMPONENT.getCode());
        if(StringUtils.isBlank(openSysConfigByCode)) throw new OpenApiDingtalkException(DingtalkResponseCode.PERSON_COMPANY_UNCONFIGURATION);
        Map<String,Object> map = JsonUtils.toObj(openSysConfigByCode, Map.class);
        String companyId = (String)map.get("company_id");
        String thirdEmployeethird = (String)map.get("third_employee_id");
        if(StringUtils.isBlank(companyId) || StringUtils.isBlank(thirdEmployeethird)) throw new OpenApiDingtalkException(DingtalkResponseCode.PERSON_COMPANY_UNCONFIGURATION);
        return DingtalkIsvAuthRespDTO.builder().companyId(companyId).thirdEmployeeId(thirdEmployeethird).type("1").corpId(mainCorpId).build();
    }

    /**
     * 获取用户userid
     *
     * @param code
     * @param corpId
     */
    private String getUserInfo(String code, String corpId) {
        String url = dingtalkHost + "user/getuserinfo";
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(code);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse oapiUserGetuserinfoResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return oapiUserGetuserinfoResponse.getUserid();
    }


    @Override
    public DingtalkIsvWebLoginInfoRespDTO webLogin(String code) {
        // 获取管理员id,验证是否为管理员
        OapiSsoGetuserinfoResponse ssoUserInfo = getSSOUserInfo(code);
        boolean isAdmin = ssoUserInfo.getIsSys();
        if (!isAdmin) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.CHECK_IS_NOT_ADMIN);
        }
        String thirdUserId = ssoUserInfo.getUserInfo().getUserid();
        String corpId = ssoUserInfo.getCorpInfo().getCorpid();
        // 获取用户企业信息
        DingtalkIsvCompany feiShuIsvCompany = getCompanyByCorpId(corpId);
        String companyId = feiShuIsvCompany.getCompanyId();
        // 根据分贝通企业id+用户三方id获取分贝通用户信息
        ThirdEmployeeRes fbEmployee = dingtalkIsvEmployeeService.getEmployeeByThirdId(companyId, thirdUserId);
        log.info("webAuth companyId is {} userId is {} fbEmployee is {}", companyId, thirdUserId, fbEmployee);
        String operatorId = dingtalkIsvEmployeeService.superAdmin(companyId);
        if (fbEmployee == null) {
            log.info("webAuth 用户不存在，添加用户");
            // 查询管理员用户基本信息
            OapiUserGetResponse userInfo = dingtalkIsvEmployeeService.getUserInfo(thirdUserId, corpId);
            // 如果用户在分贝通不存在则创建，并设置为普通管理员
            createEmployee(companyId, userInfo, corpId);
        }
        // 授权登录，并返回信息
        LoginResVO loginResVO = null;
        try {
            if (fbEmployee == null || (fbEmployee.getEmployee().getRole() != EmployeeRole.CompanyAdmin.getKey() &&
                    fbEmployee.getEmployee().getRole() != EmployeeRole.CompanySuperAdmin.getKey())) {
                loginResVO = iAuthService.loginAuthInitV5(companyId, thirdUserId, null, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.DINGTALK_MARKET.getPlatform(), CompanyLoginChannelEnum.DINGTALK_MARKET.getEntrance());
            } else {
                loginResVO = iAuthService.adminLoginAuthV5(companyId, thirdUserId, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.DINGTALK_MARKET.getPlatform(), CompanyLoginChannelEnum.DINGTALK_MARKET.getEntrance());

            }
        } catch (FinhubException e) {
            log.error("uc授权登录失败：", e);
            String errMsg = e.getMessage();
            throw new OpenApiDingtalkException(DingtalkResponseCode.WEB_AUTH_FAILED, errMsg);
        }
        List<MenuInfoVo> menuInfoVoList = iMenuService.queryLevelListByType(MenuType.WebMenu.getKey(), loginResVO.getUser_info().getId(), companyId);
        DingtalkIsvWebLoginInfoRespDTO dingtalkIsvWebLoginInfoRespDTO = DingtalkIsvWebLoginInfoRespDTO.builder().loginResVO(loginResVO).menuInfoVoList(menuInfoVoList).build();
        return dingtalkIsvWebLoginInfoRespDTO;
    }

    private OapiSsoGetuserinfoResponse getSSOUserInfo(String code) {
        String url = dingtalkHost + "sso/getuserinfo";
        OapiSsoGetuserinfoRequest request = new OapiSsoGetuserinfoRequest();
        request.setCode(code);
        request.setHttpMethod("GET");
        OapiSsoGetuserinfoResponse oapiSsoGetuserinfoResponse = dingtalkIsvClientUtils.executeWithSSOToken(url, request);
        return oapiSsoGetuserinfoResponse;
    }

    /**
     * 普通用户添加管理员
     */
    private void authUserLimit(String companyId, String employeeId, String operatorId) {
        List<String> userList = new ArrayList<>();
        userList.add(employeeId);
        EmpowerEmployeeRoleDTO empowerEmployeeRoleDTO = new EmpowerEmployeeRoleDTO();
        empowerEmployeeRoleDTO.setCompanyId(companyId);
        empowerEmployeeRoleDTO.setOperatorId(operatorId);
        empowerEmployeeRoleDTO.setRoleType(2);
        empowerEmployeeRoleDTO.setType(1);
        empowerEmployeeRoleDTO.setUserIds(userList);
        log.info("authUserLimit companyId={},employeeId={},type={},roleType={},operatorId={}", companyId, employeeId, 1, 2, operatorId);
        iThirdEmployeeService.batchSaveEmployeeRole(empowerEmployeeRoleDTO);
    }


    /**
     * 创建管理员
     *
     * @param companyId
     * @param userInfo
     * @param corpId
     */
    private void createEmployee(String companyId, OapiUserGetResponse userInfo, String corpId) {
        //获取已存在的部门id，只授权人未授权部门的人，放根部门下
        List<OpenThirdOrgUnit> openThirdOrgUnits = openThirdOrgUnitDao.listOrgUnitByCompanyIdAndOpenType(OpenType.DINGTALK_ISV.getType(), companyId);
        List<String> departmentIds = openThirdOrgUnits.stream().map(OpenThirdOrgUnit::getThirdOrgUnitId).collect(Collectors.toList());
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getOpenId());
        openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
        openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
        List<Long> userDepartmentIds = userInfo.getDepartment();
        if (!CollectionUtils.isBlank(userDepartmentIds)){
            Iterator<Long> iterator = userDepartmentIds.iterator();
            while (iterator.hasNext()) {
                Long item = iterator.next();
                if (!departmentIds.contains(StringUtils.obj2str(item))) {
                    iterator.remove();
                }
            }
        }
        //部门为空或为1，为根部门用户
        if (ObjectUtils.isEmpty(userDepartmentIds) || 1L == userDepartmentIds.get(0)) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        } else {
            openThirdEmployeeDTO.setThirdDepartmentId(StringUtils.obj2str(userDepartmentIds.get(0)));
        }
        //openThirdEmployeeDTO.setThirdEmployeeRole(EmployeeRole.CompanyAdmin.getKey());
        employeeList.add(openThirdEmployeeDTO);
        //同步
        openSyncThirdOrgService.addEmployee(OpenType.DINGTALK_ISV.getType(), companyId, employeeList);
    }


    /**
     * 根据corpId获取企业信息
     */
    private DingtalkIsvCompany getCompanyByCorpId(String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        // 1.检查企业状态,只有已完成数据初始化的企业才可以登录
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        if (dingtalkIsvCompany.getState() != CompanyAuthState.AUTH_SUCCESS.getCode()) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.COMPANY_STATE_CLOSE);
        }
        return dingtalkIsvCompany;
    }


}
