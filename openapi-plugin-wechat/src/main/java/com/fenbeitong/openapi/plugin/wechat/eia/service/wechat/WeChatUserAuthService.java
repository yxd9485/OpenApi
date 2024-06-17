package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.auth.constant.FreeLoginConstant;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.CommonFreeLoginDto;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcFetchEmployInfoReqDto;
import com.fenbeitong.openapi.plugin.support.employee.service.IFreeLoginUserService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.UserInfoResponse;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatLinkedCorpUserDetailDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.WeChatEiaGetUserDetailResponse;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.util.WeChatEiaHttpUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatGetUserResponse;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeSyncDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author lizhen
 * @date 2020/8/5
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatUserAuthService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    private WeChatEiaHttpUtils weChatEiaHttpUtils;

    @Autowired
    private WeChatEiaEmployeeService weChatEiaEmployeeService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private IFreeLoginUserService freeLoginUserService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeServiceImpl;

    private static final String LINKED_CORP_SETTING = "linked_corp_setting";

    /**
     * 企业微信用户授权登录
     *
     * @param code          企业微信的临时授权码，根据sensitiveFlag不同，能够获取的信息也不同
     * @param corpId        企业三方id
     * @param sensitiveFlag true，表示code能获取敏感信息 ；null或者false，表示code不能获取敏感信息
     * @return 免登信息
     */
    public LoginResVO auth(String code, String corpId, Boolean sensitiveFlag) {
        // 1 先校验公司的合法性
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (corpDefinition == null) {
            log.warn("微信免登，未配置该公司，请联系实施。corpId:{}", corpId);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
        UserInfoResponse thirdUserInfo = getUserInfoByCode(code, corpId);
        log.info("微信免登 根据临时授权码获取用户信息 thirdUserInfo:{}", JsonUtils.toJson(thirdUserInfo));

        //2.1 通过获取敏感信息里面的手机号，进行免登
        if (sensitiveFlag != null && sensitiveFlag) {
            return getLoginInfoByUserTicket(corpDefinition, thirdUserInfo.getUserTicket());
        }

        String appAccessToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
        // 2.2 互联企业通过手机号免登
        OpenMsgSetup linkedCorpSetting = openMsgSetupDao.selectByCompanyIdAndItemCode(corpDefinition.getAppId(), LINKED_CORP_SETTING);
        if (!ObjectUtils.isEmpty(linkedCorpSetting)) {
            return getLoginInfoByLinkedCorp(corpDefinition, thirdUserInfo.getUserId(), appAccessToken);
        }
        // 2.3 根据脚本字段进行免登
        OpenThirdScriptConfig freeAccountConfig = openThirdScriptConfigDao.getCommonScriptConfig(corpDefinition.getAppId(), EtlScriptType.USER_FREE_LOGIN);
        if (freeAccountConfig != null) {
            return getLoginInfoByScript(corpDefinition.getAppId(), thirdUserInfo.getUserId(), appAccessToken, freeAccountConfig);
        }
        //2.4 根据userId免登
        return getLoginInfoByUserId(corpDefinition, thirdUserInfo.getUserId(), appAccessToken);
    }

    /**
     * 根据微信 用户userId 或者 微信自定义字段 作为分贝通三方id，进行免登
     *
     * @param corpDefinition 公司信息
     * @param userId 微信userId
     * @param appAccessToken token
     * @return
     */
    private LoginResVO getLoginInfoByUserId(PluginCorpDefinition corpDefinition, String userId, String appAccessToken) {
        String thirdEmployeeId = weChatEiaEmployeeService.getThirdEmployeeIdByUserId(appAccessToken, userId, corpDefinition.getThirdCorpId());
        LoginResVO loginResVO = openEmployeeService.loginAuthInitWithChannelInfo(corpDefinition.getAppId(), thirdEmployeeId, "1", CompanyLoginChannelEnum.WECHAT_H5);
        log.info("通过userId免登获取UC用户信息，loginResVo:{}", JsonUtils.toJson(loginResVO));
        if (ObjectUtils.isEmpty(loginResVO)){
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_UC_USER_INFO_FAIL));
        }
        return loginResVO;
    }

    /**
     * 免登字段从脚本中获取
     *
     * @param companyId         公司id
     * @param userId            微信用户id
     * @param appAccessToken    token
     * @param freeAccountConfig 免登脚本
     * @return 用户免登信息
     */
    private LoginResVO getLoginInfoByScript(String companyId, String userId, String appAccessToken, OpenThirdScriptConfig freeAccountConfig) {
        WeChatGetUserResponse detailInfo = weChatEiaEmployeeService.getUserResponse(appAccessToken, userId);
        CommonFreeLoginDto commonFreeLoginDto = CommonFreeLoginDto.builder().companyId(companyId).build();
        LoginResVO loginResVO = freeLoginUserService.buildLoginVo(freeAccountConfig, true, detailInfo, commonFreeLoginDto, new HashMap<>(), CompanyLoginChannelEnum.WECHAT_H5);
        log.info("通过脚本免登获取UC用户信息，loginResVo:{}", JsonUtils.toJson(loginResVO));
        if (ObjectUtils.isEmpty(loginResVO)){
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_UC_USER_INFO_FAIL));
        }
        return loginResVO;
    }

    /**
     * 互联企业根据手机号免登
     * 对于2022年8月之前，可以根据用户id获取手机号的互联企业适用
     *
     * @param corpDefinition 公司信息
     * @param userId         企业微信用户id
     * @param appAccessToken 访问企业微信接口token
     * @return 用户免登信息
     */
    private LoginResVO getLoginInfoByLinkedCorp(PluginCorpDefinition corpDefinition, String userId, String appAccessToken) {
        //互联企业
        WeChatLinkedCorpUserDetailDTO linkedCorpUserDetail = weChatEiaEmployeeService.getLinkedCorpUserDetail(appAccessToken, userId);
        if (ObjectUtils.isEmpty(linkedCorpUserDetail) || ObjectUtils.isEmpty(linkedCorpUserDetail.getUserInfo())) {
            log.info("查询企业互联人员详情失败，corpId：{},userId:{}", corpDefinition.getThirdCorpId(), userId);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_EMPLOYEE_NOT_EXISTS));
        }
        String mobile = linkedCorpUserDetail.getUserInfo().getMobile();
        UcFetchEmployInfoReqDto ucFetchEmployInfoReqDto = new UcFetchEmployInfoReqDto();
        ucFetchEmployInfoReqDto.setCompanyId(corpDefinition.getAppId());
        ucFetchEmployInfoReqDto.setPhone(mobile);
        LoginResVO loginResVO = openEmployeeService.fetchLoginAuthInfoByPhoneNum(ucFetchEmployInfoReqDto, CompanyLoginChannelEnum.WECHAT_H5);
        log.info("互联企业获取UC登录信息，loginResVo : {}", JsonUtils.toJson(loginResVO));
        if (ObjectUtils.isEmpty(loginResVO)) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_UC_USER_INFO_FAIL));
        }
        return loginResVO;
    }

    /**
     * 通过获取用户敏感信息（手机号），直接进行免登
     *
     * @param corpDefinition 公司信息
     * @param userTicket     获取敏感信息的用户凭证
     * @return 用户免登信息
     */
    private LoginResVO getLoginInfoByUserTicket(PluginCorpDefinition corpDefinition, String userTicket) {
        // 如果用户拒绝授权，user_ticket为空，直接抛异常。避免user_ticket为空时，直接调用企业微信接口
        if (StringUtils.isBlank(userTicket)) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_WECHAT_USER_MOBILE_FAIL));
        }
        WeChatEiaGetUserDetailResponse userDetailResponse = getUserDetailByTicket(userTicket, corpDefinition.getThirdCorpId());
        if (StringUtils.isBlank(userDetailResponse.getMobile())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_WECHAT_USER_MOBILE_FAIL));
        }
        UcFetchEmployInfoReqDto ucFetchEmployInfoReqDto = new UcFetchEmployInfoReqDto();
        ucFetchEmployInfoReqDto.setCompanyId(corpDefinition.getAppId());
        ucFetchEmployInfoReqDto.setPhone(userDetailResponse.getMobile());
        LoginResVO loginResVO = openEmployeeService.fetchLoginAuthInfoByPhoneNum(ucFetchEmployInfoReqDto, CompanyLoginChannelEnum.WECHAT_H5);
        log.info(" 获取敏感信息（手机号）进行免登 loginResVO: {}", JsonUtils.toJson(loginResVO));
        if (ObjectUtils.isEmpty(loginResVO)) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_UC_USER_INFO_FAIL));
        }
        return loginResVO;
    }

    /**
     * 废弃，目前没有企业在用，并且跟自定义字段获取三方id有功能重叠
     */
    @Deprecated
    private String queryThirdUserId(String companyId , UserInfoResponse thirdUserInfo , WeChatGetUserResponse weChatGetUserResponse, String appAccessToken) {
        String userId = null == weChatGetUserResponse ? thirdUserInfo.getUserId() : weChatGetUserResponse.getUserId();
        log.info("原始三方 userId {}",userId);
        try {
            // 三方ID是否取配置信息
            OpenThirdScriptConfig thirdUserIdConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.THIRD_USER_ID_SYNC);
            if( null == thirdUserIdConfig ){
                log.info("未配置三方 userId 脚本 , 返回原始数据 {}",userId);
                return userId;
            }
            log.info("已配置三方 userId 脚本 , 开始取自定义字段 , companyId : {}",companyId);
            if ( null == weChatGetUserResponse){
                log.info("初始用户信息为空,开始查询微信用户详情信息");
                weChatGetUserResponse = weChatEiaEmployeeService.getUserResponse(appAccessToken,thirdUserInfo.getUserId());
            }
            if ( null != weChatGetUserResponse) {
                log.info("查询微信用户详情信息: WeChatIsvGetUserResponse : {}",JsonUtils.toJson(weChatGetUserResponse));
            }
            userId = freeLoginUserService.getAuthFreeLoginLabel(weChatGetUserResponse, FreeLoginConstant.THIRD_ID_VALUE, thirdUserIdConfig);
        } catch (Exception e) {
            log.info("转换用户三方id失败 : {} ",e.getMessage());
        }
        log.info("三方 userId {}",userId);
        return userId;
    }


    public UserInfoResponse getUserInfoByCode(String code, String corpId) {
        String getUserUrl = wechatHost + "/cgi-bin/user/getuserinfo";
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        String res = weChatEiaHttpUtils.getWithAppAccessToken(getUserUrl, param, corpId);
        UserInfoResponse thirdUserInfo = JsonUtils.toObj(res, UserInfoResponse.class);
        if (thirdUserInfo == null || (Optional.ofNullable(thirdUserInfo.getErrcode()).orElse(-1) != 0)) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_EMPLOYEE_NOT_EXISTS));
        }
        return thirdUserInfo;
    }

    /**
     * 获取访问用户敏感信息
     *
     * @param userTicket 成员票据
     * @param corpId 三方企业ID
     * @return 用户详情（包含敏感信息）
     */
    public WeChatEiaGetUserDetailResponse getUserDetailByTicket(String userTicket, String corpId) {
        String getUserDetailUrl = wechatHost + "/cgi-bin/user/getuserdetail?access_token=";
        Map<String, Object> param = new HashMap<>();
        param.put("user_ticket", userTicket);
        String res = weChatEiaHttpUtils.postJsonWithAppAccessToken(getUserDetailUrl, JsonUtils.toJson(param), corpId);
        WeChatEiaGetUserDetailResponse userDetailResponse = JsonUtils.toObj(res, WeChatEiaGetUserDetailResponse.class);
        if (Optional.ofNullable(userDetailResponse).map(WeChatEiaGetUserDetailResponse::getErrCode).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_WECHAT_USER_DETAIL_FAIL));
        }
        return userDetailResponse;
    }

    /**
     * 根据企业微信用户敏感信息更新手机号
     *
     * @param companyId             三方企业id
     * @param userDetailResponse 企业微信用户信息
     */
    public void syncEmployeePhone(String companyId, WeChatEiaGetUserDetailResponse userDetailResponse) {
        //根据三方员工id以及三方企业id，查询员工是否存在
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setCompanyId(companyId);
        thirdEmployeeContract.setUserType(2);
        thirdEmployeeContract.setEmployeeId(userDetailResponse.getUserId());
        ThirdEmployeeRes thirdEmployeeRes = openEmployeeServiceImpl.getUcEmployeeInfo(thirdEmployeeContract);
        if (!Optional.ofNullable(thirdEmployeeRes).map(ThirdEmployeeRes::getEmployee).isPresent()) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_UC_USER_INFO_FAIL));
        }
        // 更新手机号
        EmployeeSyncDTO employeeSyncDTO = new EmployeeSyncDTO();
        employeeSyncDTO.setEmployeeId(thirdEmployeeRes.getEmployee().getId());
        employeeSyncDTO.setCompanyId(thirdEmployeeRes.getEmployee().getCompanyId());
        employeeSyncDTO.setPhoneNum(userDetailResponse.getMobile());
        try {
            openEmployeeServiceImpl.updateUserPhone(employeeSyncDTO);
        } catch (FinhubException ex) {
            log.warn("请求UC更新手机号失败,employeeSyncDTO:{}", employeeSyncDTO, ex);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_UPDATE_USER_PHONE_FAIL));
        }
    }
}
