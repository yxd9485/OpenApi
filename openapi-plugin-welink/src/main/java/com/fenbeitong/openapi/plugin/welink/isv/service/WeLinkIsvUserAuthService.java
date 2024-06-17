package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcCreateEmployeeRequest;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.*;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.util.WeLinkIsvHttpUtils;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lizhen on 2020/4/15.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvUserAuthService {

    @Value("${welink.api-host}")
    private String welinkHost;

    @Value("${welink.isv.appId}")
    private String appId;

    @Value("${welink.isv.appSecret}")
    private String appSecret;

    @Value("${host.openplus}")
    private String hostOpenplus;

    @Autowired
    private UserCenterService userCenterService;
    @DubboReference(check = false)
    private IAuthService iAuthService;
    @DubboReference(check = false)
    private IMenuService iMenuService;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;
    @Autowired
    private RestHttpUtils httpUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;
    @Autowired
    private WeLinkIsvEmployeeService weLinkIsvEmployeeService;
    @Autowired
    private WeLinkIsvHttpUtils weLinkIsvHttpUtils;
    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;

    /**
     * welink用户授权登录
     */
    public WeLinkIsvAuthRespDTO auth(String code) {
        WeLinkIsvAccessTokenToUserRespDTO thirdUserInfo = codeToUser(code);
        if (thirdUserInfo == null)
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_CORP_EMPLOYEE_NOT_EXISTS);
        String corpId = thirdUserInfo.getTenantId();
        String thirdUserId = thirdUserInfo.getUserId();
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = getCompanyByCorpId(corpId);
        return WeLinkIsvAuthRespDTO.builder().companyId(weLinkIsvCompanyTrial.getCompanyId()).thirdEmployeeId(thirdUserId).build();
    }


    /**
     * welink服务商后台授权登录
     */
    public WeLinkIsvWebLoginInfoRespDTO webLogin(String code, String state) throws UnsupportedEncodingException {
        // 获取管理员id,验证是否为管理员
        String accessToken = authCodeToAccessToken(code, state);
        boolean isAdmin = accessTokenToIsAdmin(accessToken);
        if (!isAdmin) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_IS_NOT_ADMIN);
        }
        WeLinkIsvAccessTokenToUserRespDTO weLinkIsvAccessTokenToUserRespDto = accessTokenToUser(accessToken);
        // 获取用户企业信息
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = getCompanyByCorpId(weLinkIsvAccessTokenToUserRespDto.getTenantId());
        String companyId = weLinkIsvCompanyTrial.getCompanyId();
        String thirdUserId = weLinkIsvAccessTokenToUserRespDto.getUserId();
        // 根据分贝通企业id+用户三方id获取分贝通用户信息
        ThirdEmployeeRes fbEmployee = weLinkIsvEmployeeService.getEmployeeByThirdId(companyId, thirdUserId);
        log.info("webAuth companyId is {} userId is {} fbEmployee is {}", companyId, thirdUserId, fbEmployee);
        String operatorId = weLinkIsvEmployeeService.superAdmin(companyId);
        if (fbEmployee == null) {
            log.info("webAuth 用户不存在，添加用户");
            // 查询管理员用户基本信息
            WeLinkIsvUserSimpleRespDTO weLinkIsvUserSimpleRespDTO = weLinkIsvEmployeeService.userSimple(weLinkIsvAccessTokenToUserRespDto.getUserId(), weLinkIsvAccessTokenToUserRespDto.getTenantId());
            // 如果用户在分贝通不存在则创建
            createEmployee(companyId, weLinkIsvUserSimpleRespDTO, operatorId, weLinkIsvCompanyTrial.getCorpId());
        }
        // 授权登录，并返回信息
        LoginResVO loginResVO = null;
        try {
            if (fbEmployee == null || (fbEmployee.getEmployee().getRole() != EmployeeRole.CompanyAdmin.getKey() &&
                    fbEmployee.getEmployee().getRole() != EmployeeRole.CompanySuperAdmin.getKey())) {
                loginResVO = iAuthService.loginAuthInitV5(companyId, thirdUserId, null, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.WELINK_MARKET.getPlatform(), CompanyLoginChannelEnum.WELINK_MARKET.getEntrance());
            } else {
                loginResVO = iAuthService.adminLoginAuthV5(companyId, thirdUserId, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.WELINK_MARKET.getPlatform(), CompanyLoginChannelEnum.WELINK_MARKET.getEntrance());
            }
        } catch (FinhubException e) {
            log.error("uc授权登录失败：", e);
            String errMsg = e.getMessage();
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_WEB_AUTH_FAILED, errMsg);
        }
        List<MenuInfoVo> menuInfoVoList = iMenuService.queryLevelListByType(MenuType.WebMenu.getKey(), loginResVO.getUser_info().getId(), companyId);
        WeLinkIsvWebLoginInfoRespDTO weLinkIsvWebLoginInfo = WeLinkIsvWebLoginInfoRespDTO.builder().loginResVO(loginResVO).menuInfoVoList(menuInfoVoList).build();
        return weLinkIsvWebLoginInfo;
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
     * 将登陆信息缓存5分钟，客户端跳转需要使用生成的token换取登录信息
     * token生成方式暂时使用业务数据MD5的方式
     */
    private String cacheWebLoginInfo(LoginResVO loginResVO, List<MenuInfoVo> menuInfoVoList) {
        WeLinkIsvWebLoginInfoRespDTO weLinkIsvWebLoginInfo = WeLinkIsvWebLoginInfoRespDTO.builder().loginResVO(loginResVO).menuInfoVoList(menuInfoVoList).build();
        String loginInfoJson = JsonUtils.toJson(weLinkIsvWebLoginInfo);
        String token = StringUtils.md5(loginInfoJson);
        redisTemplate.opsForValue().set(token, loginInfoJson, 5, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 创建管理员
     *
     * @param companyId
     * @param weLinkIsvUserSimpleRespDTO
     * @param operatorId
     */
    private void createEmployee(String companyId, WeLinkIsvUserSimpleRespDTO weLinkIsvUserSimpleRespDTO, String operatorId, String corpId) {
        List<UcCreateEmployeeRequest.CreateEmployeeEntity> createEmployeeEntityList = new ArrayList<>();
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
        openThirdEmployeeDTO.setCompanyId(companyId);
        openThirdEmployeeDTO.setThirdDepartmentId(weLinkIsvUserSimpleRespDTO.getDeptCode());
        openThirdEmployeeDTO.setThirdEmployeeId(weLinkIsvUserSimpleRespDTO.getUserId());
        openThirdEmployeeDTO.setThirdEmployeeName(weLinkIsvUserSimpleRespDTO.getUserNameCn());
        if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
            openThirdEmployeeDTO.setThirdDepartmentId(corpId);
        }
        openThirdEmployeeDTO.setThirdEmployeeRole(EmployeeRole.CompanyNormal.getKey());
        employeeList.add(openThirdEmployeeDTO);
        //同步
        openSyncThirdOrgService.addEmployee(OpenType.WELINK_ISV.getType(), companyId, employeeList);
    }


    /**
     * 根据corpId获取企业信息
     */
    private WeLinkIsvCompanyTrial getCompanyByCorpId(String corpId) {
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(corpId);
        // 1.检查企业状态,只有已完成数据初始化的企业才可以登录
        if (weLinkIsvCompanyTrial == null)
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_COMPANY_UNDEFINED);
        if (weLinkIsvCompanyTrial.getState() != CompanyAuthState.AUTH_SUCCESS.getCode())
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_COMPANY_STATE_CLOSE);
        return weLinkIsvCompanyTrial;
    }


    /**
     * authCode换取accessToken，用于web免登
     *
     * @param code
     * @return
     */
    private String authCodeToAccessToken(String code, String state) {
        String url = welinkHost + WeLinkIsvConstant.AUTH_CODE_TO_ACCESS_TOKEN_URL;
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("grant_type", "authorization_code");
        requestMap.add("code", code);
        requestMap.add("client_id", appId);
        requestMap.add("client_secret", appSecret);
        requestMap.add("redirect_uri", hostOpenplus + WeLinkIsvConstant.CALLBACK_BUSINESS_URL);
        requestMap.add("state", state);
        String resJson = httpUtil.postForm(url, requestMap);
        WeLinkIsvAuthCodeToAccessTokenRespDTO res = JsonUtils.toObj(resJson, WeLinkIsvAuthCodeToAccessTokenRespDTO.class);
        if (res == null || !"60001".equals(res.getCode())) {
            log.info("authCodeToAccessToken 失败,res={}", resJson);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_AUTH_CODE_TO_ACCESS_TOKEN_FAILED);
        }
        return res.getAccessToken();
    }

    /**
     * access_token查询是否为管理员
     */
    private boolean accessTokenToIsAdmin(String accessToken) {
        String url = welinkHost + WeLinkIsvConstant.ACCESS_TOKEN_TO_IS_ADMIN;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-wlk-Authorization", accessToken);
        String res = httpUtil.get(url, httpHeaders, null);
        WeLinkIsvIsAdminRespDTO weLinkIsvIsAdminRespDto = JsonUtils.toObj(res, WeLinkIsvIsAdminRespDTO.class);
        if (weLinkIsvIsAdminRespDto == null || !"0".equals(weLinkIsvIsAdminRespDto.getCode())) {
            log.info("accessTokenToIsAdmin 失败,res={}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_AUTH_CODE_TO_IS_ADMIN_FAILED);
        }
        return weLinkIsvIsAdminRespDto.isAdmin();
    }

    /**
     * accessToken换取user信息，用于web免登
     *
     * @param accessToken
     */
    private WeLinkIsvAccessTokenToUserRespDTO accessTokenToUser(String accessToken) {
        String url = welinkHost + WeLinkIsvConstant.ACCESS_TOKEN_TO_USER_URL;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-wlk-Authorization", accessToken);
        String res = httpUtil.get(url, httpHeaders, null);
        WeLinkIsvAccessTokenToUserRespDTO weLinkIsvAccessTokenToUserRespDto = JsonUtils.toObj(res, WeLinkIsvAccessTokenToUserRespDTO.class);
        if (weLinkIsvAccessTokenToUserRespDto == null || !"0".equals(weLinkIsvAccessTokenToUserRespDto.getCode())) {
            log.info("accessTokenToUser 失败,res={}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_AUTH_CODE_TO_USER_FAILED);
        }
        return weLinkIsvAccessTokenToUserRespDto;
    }


    /**
     * 通过免登授权码查询用户userId
     *
     * @param code
     * @return
     */
    private WeLinkIsvAccessTokenToUserRespDTO codeToUser(String code) {
        String url = welinkHost + WeLinkIsvConstant.CODE_TO_USER_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, param, appId);
        WeLinkIsvAccessTokenToUserRespDTO weLinkIsvAccessTokenToUserRespDto = JsonUtils.toObj(res, WeLinkIsvAccessTokenToUserRespDTO.class);
        if (weLinkIsvAccessTokenToUserRespDto == null || !"0".equals(weLinkIsvAccessTokenToUserRespDto.getCode())) {
            log.info("codeToUser 失败,res={}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_AUTH_CODE_TO_USER_FAILED);
        }
        return weLinkIsvAccessTokenToUserRespDto;
    }

}

