package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuAuthenRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuAuthService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.*;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户授权service
 *
 * @author lizhen
 * @date 2020/6/5
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvUserAuthSrvice extends AbstractFeiShuAuthService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @DubboReference(check = false)
    private IAuthService iAuthService;
    @DubboReference(check = false)
    private IMenuService iMenuService;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;

    /**
     * 应用免登
     *
     * @param code
     * @return
     */
    public FeiShuIsvAuthRespDTO auth(String code) {
        FeiShuIsvTokenLoginValidateRespDTO feiShuIsvTokenLoginValidateRespDTO = tokenLoginValidate(code);
        String tenantKey = feiShuIsvTokenLoginValidateRespDTO.getData().getTenantKey();
        String openId = feiShuIsvTokenLoginValidateRespDTO.getData().getOpenId();
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(tenantKey);
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String companyId = feiShuIsvCompany.getCompanyId();
        return FeiShuIsvAuthRespDTO.builder().companyId(companyId).thirdEmployeeId(openId).build();
    }

    /**
     * h5免登
     *
     * @param code
     * @return
     */
    public FeiShuIsvAuthRespDTO authH5(String code) {
        FeiShuAuthenRespDTO.FeiShuAuthenData feiShuAuthenData = webLoginValidate(code);
        String tenantKey = feiShuAuthenData.getTenantKey();
        String openId = feiShuAuthenData.getOpenId();
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(tenantKey);
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String companyId = feiShuIsvCompany.getCompanyId();
        return FeiShuIsvAuthRespDTO.builder().companyId(companyId).thirdEmployeeId(openId).build();
    }


    /**
     * 服务商后台授权登录
     */
    public FeiShuIsvWebLoginInfoRespDTO webLogin(String code) throws UnsupportedEncodingException {
        // 获取管理员id,验证是否为管理员
        FeiShuAuthenRespDTO.FeiShuAuthenData backendLoginValidateInfo = webLoginValidate(code);
        String thirdUserId = backendLoginValidateInfo.getOpenId();
        String corpId = backendLoginValidateInfo.getTenantKey();
        boolean isAdmin = isAdmin(thirdUserId, corpId);
        if (!isAdmin) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.CHECK_IS_NOT_ADMIN);
        }
        // 获取用户企业信息
        FeishuIsvCompany feiShuIsvCompany = getCompanyByCorpId(corpId);
        String companyId = feiShuIsvCompany.getCompanyId();
        // 根据分贝通企业id+用户三方id获取分贝通用户信息
        ThirdEmployeeRes fbEmployee = feiShuIsvEmployeeService.getEmployeeByThirdId(companyId, thirdUserId);
        log.info("webAuth companyId is {} userId is {} fbEmployee is {}", companyId, thirdUserId, fbEmployee);
        String operatorId = feiShuIsvEmployeeService.superAdmin(companyId);
        if (fbEmployee == null) {
            log.info("webAuth 用户不存在，添加用户");
            // 查询管理员用户基本信息
            FeiShuUserInfoDTO userInfo = feiShuIsvEmployeeService.getUserInfo(FeiShuConstant.ID_TYPE_OPEN_ID, thirdUserId, corpId);
            if (userInfo == null) {
                throw new OpenApiFeiShuException(FeiShuResponseCode.WEB_AUTH_FAILED, "，应用可见范围不包含当前用户");
            }
            // 如果用户在分贝通不存在则创建
            createEmployee(companyId, userInfo, corpId);
        }
        // 授权登录，并返回信息
        LoginResVO loginResVO = null;
        try {
            if (fbEmployee == null || (fbEmployee.getEmployee().getRole() != EmployeeRole.CompanyAdmin.getKey() &&
                    fbEmployee.getEmployee().getRole() != EmployeeRole.CompanySuperAdmin.getKey())) {
                loginResVO = iAuthService.loginAuthInitV5(companyId, thirdUserId, null, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.LARK_MARKET.getPlatform(), CompanyLoginChannelEnum.LARK_MARKET.getEntrance());
            } else {
                loginResVO = iAuthService.adminLoginAuthV5(companyId, thirdUserId, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.LARK_MARKET.getPlatform(), CompanyLoginChannelEnum.LARK_MARKET.getEntrance());
            }
        } catch (FinhubException e) {
            log.error("uc授权登录失败：", e);
            String errMsg = e.getMessage();
            throw new OpenApiFeiShuException(FeiShuResponseCode.WEB_AUTH_FAILED, errMsg);
        }
        List<MenuInfoVo> menuInfoVoList = iMenuService.queryLevelListByType(MenuType.WebMenu.getKey(), loginResVO.getUser_info().getId(), companyId);
        FeiShuIsvWebLoginInfoRespDTO feiShuIsvWebLoginInfoRespDTO = FeiShuIsvWebLoginInfoRespDTO.builder().loginResVO(loginResVO).menuInfoVoList(menuInfoVoList).build();
        return feiShuIsvWebLoginInfoRespDTO;
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
     * 应用免登code校验
     */
    private FeiShuIsvTokenLoginValidateRespDTO tokenLoginValidate(String code) {
        String url = feishuHost + FeiShuConstant.TOKEN_LOGIN_VALIDATE_URL;
        JSONObject json = new JSONObject();
        json.put("code", code);
        String res = feiShuIsvHttpUtils.postJsonWithAppAccessToken(url, json.toJSONString());
        FeiShuIsvTokenLoginValidateRespDTO feiShuIsvTokenLoginValidateRespDTO = JsonUtils.toObj(res, FeiShuIsvTokenLoginValidateRespDTO.class);
        if (feiShuIsvTokenLoginValidateRespDTO == null || 0 != feiShuIsvTokenLoginValidateRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.TOKEN_LOGIN_VALIDATE_FAILED);
        }
        return feiShuIsvTokenLoginValidateRespDTO;
    }


    /**
     * 校验应用管理员
     *
     * @param openId
     * @param corpId
     * @return
     */
    private boolean isAdmin(String openId, String corpId) {
        String url = feishuHost + FeiShuConstant.IS_USER_ADMIN_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("open_id", openId);
        String res = feiShuIsvHttpUtils.getWithTenantAccessToken(url, param, corpId);
        FeiShuIsvIsUserAdminRespDTO feiShuIsvIsUserAdminRespDTO = JsonUtils.toObj(res, FeiShuIsvIsUserAdminRespDTO.class);
        if (feiShuIsvIsUserAdminRespDTO == null || 0 != feiShuIsvIsUserAdminRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.CHECK_IS_USER_ADMIN_FAILED);
        }
        return feiShuIsvIsUserAdminRespDTO.getData().isAppAdmin();
    }


    /**
     * 根据corpId获取企业信息
     */
    private FeishuIsvCompany getCompanyByCorpId(String corpId) {
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        // 1.检查企业状态,只有已完成数据初始化的企业才可以登录
        if (feiShuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }

        if (feiShuIsvCompany.getState() != CompanyAuthState.AUTH_SUCCESS.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.COMPANY_STATE_CLOSE);
        }
        return feiShuIsvCompany;
    }

    /**
     * 创建管理员
     *
     * @param companyId
     * @param userInfo
     * @param corpId
     */
    private void createEmployee(String companyId, FeiShuUserInfoDTO userInfo, String corpId) {
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        if (ObjectUtils.isEmpty(userInfo.getDepartments())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        } else {
            openThirdEmployeeDTO.setThirdDepartmentId(userInfo.getDepartments().get(0));
        }
        openThirdEmployeeDTO.setThirdEmployeeId(userInfo.getOpenId());
        openThirdEmployeeDTO.setThirdEmployeeName(userInfo.getName());
        openThirdEmployeeDTO.setThirdEmployeeEmail(userInfo.getEmail());
        openThirdEmployeeDTO.setThirdEmployeePhone(userInfo.getMobile());
        openThirdEmployeeDTO.setThirdEmployeeGender(userInfo.getGender());
        if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        }
        openThirdEmployeeDTO.setThirdEmployeeRole(EmployeeRole.CompanyNormal.getKey());
        employeeList.add(openThirdEmployeeDTO);
        //同步
        openSyncThirdOrgService.addEmployee(OpenType.FEISHU_ISV.getType(), companyId, employeeList);
    }

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuIsvHttpUtils;
    }
}
