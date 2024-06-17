package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcCreateEmployeeRequest;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.UserInfoResponse;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatAuthRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvLoginInfoRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvLoginInfoResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvWebLoginInfo;
import com.fenbeitong.openapi.plugin.wechat.isv.enums.CompanyAuthState;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.*;
import static com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant.GET_USER_INFO_URL;
import static com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant.GET_WEB_LOGIN_INFO_URL;

/**
 * 企业微信ISV用户授权
 * Created by lizhen on 2020/3/19.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvUserAuthService {


    @Autowired
    private WeChatIsvHttpUtils wechatIsvHttpUtils;
    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;
    @Autowired
    private WeChatIsvCompanyProviderTokenService weChatIsvCompanyProviderTokenService;
    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;
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
    private WeChatIsvHttpUtils weChatIsvHttpUtils;

    /**
     * 企业微信用户授权登录
     */
    public WeChatAuthRespDTO auth(String code) {
        UserInfoResponse thirdUserInfo = getUserInfo(code);
        if (thirdUserInfo == null)
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_CORP_EMPLOYEE_NOT_EXISTS));
        String corpId = thirdUserInfo.getCorpId();
        String thirdUserId = thirdUserInfo.getUserId();
        WeChatIsvCompany weChatIsvCompany = getCompanyByCorpId(corpId);
        return WeChatAuthRespDTO.builder().companyId(weChatIsvCompany.getCompanyId()).thirdEmployeeId(thirdUserId).build();
    }

    /**
     * 获取访问用户身份
     */
    private UserInfoResponse getUserInfo(String code) {
        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        String res = wechatIsvHttpUtils.getJsonWithSuiteAccessToken(GET_USER_INFO_URL, param);
        return JsonUtils.toObj(res, UserInfoResponse.class);
    }

    /**
     * 企业微信web后台使用token换取登录信息
     */
    public WeChatIsvWebLoginInfo webAuth(String token) {
        String loginInfoJson = (String) redisTemplate.opsForValue().get(token);
        return JsonUtils.toObj(loginInfoJson, WeChatIsvWebLoginInfo.class);
    }

    /**
     * 企业微信服务商后台授权登录
     */
    public String webLogin(String authCode) throws UnsupportedEncodingException {
        WeChatIsvWebLoginInfo weChatIsvWebLoginInfo = webAuthCodeLogin(authCode);
        return cacheWebLoginInfo(weChatIsvWebLoginInfo);
    }

    /**
     * 企业微信服务商后台授权登录新
     */
    public WeChatIsvWebLoginInfo webAuthCodeLogin(String authCode) throws UnsupportedEncodingException {
        // 获取企业微信管理员用户信息
        WeChatIsvLoginInfoResponse isvLoginInfo = getWebLoginInfo(authCode);
        //1.创建者 2.内部系统管理员 3.外部系统管理员 4.分级管理员 5.成员
//        if (!isvLoginInfo.getUserType().equals(1) && !isvLoginInfo.getUserType().equals(2) && !isvLoginInfo.getUserType().equals(4)) {
//            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_USER_NOT_ADMIN));
//        }
        // 获取企业微信用户企业信息
        WeChatIsvCompany weChatIsvCompany = getCompanyByCorpId(isvLoginInfo.getCorpInfo().getCorpId());
        String companyId = weChatIsvCompany.getCompanyId();
        Integer agentid = weChatIsvCompany.getAgentid();
        AtomicBoolean isAppAdmin = new AtomicBoolean(false);
        List<WeChatIsvLoginInfoResponse.LoginInfoAgent> agentList = isvLoginInfo.getAgentList();
        if (agentList != null) {
            agentList.forEach(agent ->{
                if(agentid.equals(agent.getAgentId())) {
                    isAppAdmin.set(true);
                }
            });
        }
        if (!isAppAdmin.get()) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_USER_NOT_ADMIN));
        }
        String thirdUserId = isvLoginInfo.getUserInfo().getUserId();
        // 根据分贝通企业id+企业微信用户id获取分贝通用户信息
        return webLogin(companyId, thirdUserId);
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
    private String cacheWebLoginInfo(WeChatIsvWebLoginInfo weChatIsvWebLoginInfo) {
        String loginInfoJson = JsonUtils.toJson(weChatIsvWebLoginInfo);
        String token = StringUtils.md5(loginInfoJson);
        redisTemplate.opsForValue().set(token, loginInfoJson, 5, TimeUnit.MINUTES);
        return token;
    }

    private void createEmployee(String companyId, String thirdEmployeeId, String operatorId) {
        List<UcCreateEmployeeRequest.CreateEmployeeEntity> createEmployeeEntityList = new ArrayList<>();
        Random random = new Random();
        int randomInt = random.nextInt(9999);
        DecimalFormat df = new DecimalFormat("0000");
        // 企业微信用户手机号需要使用虚拟手机号，300开头11位
        String phone = "300" + DateUtils.toStr(DateUtils.now(), "MMdd") + df.format(randomInt);
        createEmployeeEntityList.add(UcCreateEmployeeRequest.CreateEmployeeEntity.builder()
                .name("企业微信用户")
                .phone(phone)
                .orgUnitId(companyId)
                .thirdEmployeeId(thirdEmployeeId)
                .role(EmployeeRole.CompanyNormal.getKey()).build());
        UcCreateEmployeeRequest req = UcCreateEmployeeRequest.builder()
                .companyId(companyId)
                .operatorId(operatorId)
                .employeeList(createEmployeeEntityList).build();
        userCenterService.createEmployee(req);
    }


    /**
     * 根据企业微信corpId获取企业信息
     */
    private WeChatIsvCompany getCompanyByCorpId(String corpId) {
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);

        // 1.检查企业状态,只有已完成数据初始化的企业才可以登录
        if (weChatIsvCompany == null)
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
        if (weChatIsvCompany.getState() != CompanyAuthState.AUTH_SUCCESS.getCode())
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_STATE_CLOSE));
        return weChatIsvCompany;
    }

    /**
     * 获取企业微信服务商后台登录人信息
     */
    private WeChatIsvLoginInfoResponse getWebLoginInfo(String authCode) {
        WeChatIsvLoginInfoRequest req = WeChatIsvLoginInfoRequest.builder().authCode(authCode).build();
        String resJson = weChatIsvHttpUtils.postJsonWithProviderAccessToken(GET_WEB_LOGIN_INFO_URL, JsonUtils.toJson(req));
        WeChatIsvLoginInfoResponse res = JsonUtils.toObj(resJson, WeChatIsvLoginInfoResponse.class);
        log.info("getLoginInfo res is {}", res);
        if (res == null || res.getUserInfo() == null)
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_CORP_EMPLOYEE_NOT_EXISTS));
        return res;
    }

    /**
     * 企业微信服务商后台授权登录新
     */
    public WeChatIsvWebLoginInfo webLogin(String companyId, String thirdUserId) {
        // 根据分贝通企业id+企业微信用户id获取分贝通用户信息
        ThirdEmployeeRes fbEmployee = weChatIsvEmployeeService.getEmployeeByThirdId(companyId, thirdUserId);
        log.info("webAuth companyId is {} userId is {} fbEmployee is {}", companyId, thirdUserId, fbEmployee);
        String operatorId = weChatIsvEmployeeService.superAdmin(companyId);
        if (fbEmployee == null) {
            log.info("webAuth 用户不存在，添加用户");
            // 如果用户在分贝通不存在则创建
            createEmployee(companyId, thirdUserId, operatorId);
        }

        // 授权登录，并返回信息
        LoginResVO loginResVO = null;
        try {
            if (fbEmployee == null || (fbEmployee.getEmployee().getRole() != EmployeeRole.CompanyAdmin.getKey() &&
                    fbEmployee.getEmployee().getRole() != EmployeeRole.CompanySuperAdmin.getKey())) {
                loginResVO = iAuthService.loginAuthInitV5(companyId, thirdUserId, null, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.WECHAT_MARKET.getPlatform(), CompanyLoginChannelEnum.WECHAT_MARKET.getEntrance());
            } else {
                loginResVO = iAuthService.adminLoginAuthV5(companyId, thirdUserId, IdTypeEnums.THIRD_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.WECHAT_MARKET.getPlatform(), CompanyLoginChannelEnum.WECHAT_MARKET.getEntrance());
            }
        } catch (FinhubException e) {
            log.error("uc授权登录失败：", e);
            String errMsg = e.getMessage();
            throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_LOGIN_FAILEG), errMsg);
        }
        List<MenuInfoVo> menuInfoVoList = iMenuService.queryLevelListByType(MenuType.WebMenu.getKey(), loginResVO.getUser_info().getId(), companyId);
        WeChatIsvWebLoginInfo weChatIsvWebLoginInfo = WeChatIsvWebLoginInfo.builder().loginResVO(loginResVO).menuInfoVoList(menuInfoVoList).build();
        return weChatIsvWebLoginInfo;
    }

}
