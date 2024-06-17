package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.FundAccountModelType;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatRedisKeyConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvWebLoginInfo;
import com.fenbeitong.openapi.plugin.wechat.isv.enums.CompanyAuthState;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyAuthorizeDto;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractInfoDTO;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractVo;
import com.fenbeitong.usercenter.api.model.enums.company.*;
import com.fenbeitong.usercenter.api.model.enums.employee.EmployeeCert;
import com.fenbeitong.usercenter.api.model.enums.employee.GenderType;
import com.fenbeitong.usercenter.api.model.enums.function.PackageType;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode.WECHAT_ISV_COMPANY_UNDEFINED;

/**
 * 企业微信三方应用企业授权服务
 * Created by log.chang on 2020/3/12.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvCompanyAuthService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Value("${wechat.isv.suite-id}")
    private String suiteId;

    @Value("${wechat.isv.suite-secret}")
    private String suiteSecret;

    @Value("${wechat.isv.authtype}")
    private String authtype;

    @Value("${wechat.isv.templateId}")
    private String templateId;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DubboReference(check = false)
    private ICompanyNewInfoService iCompanyNewInfoService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private WeChatIsvHttpUtils wechatIsvHttpUtil;

    @Autowired
    private WeChatIsvOrganizationService weChatIsvOrganizationService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @Autowired
    private WeChatIsvPullThirdOrgService weChatIsvPullThirdOrgService;

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private WeChatIsvUserAuthService weChatIsvUserAuthService;

    @Autowired
    private WeChatIsvMessageService weChatIsvMessageService;
    /**
     * 企业授权
     *
     * @param decryptMsg
     */
    public void companyAuth(String decryptMsg) {
        log.info("wechat isv companyAuth, 开始处理企业授权");
        WeChatIsvCompanyAuthDecryptBody weChatIsvCompanyAuthDecryptBody = (WeChatIsvCompanyAuthDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvCompanyAuthDecryptBody.class);
        String authCode = weChatIsvCompanyAuthDecryptBody.getAuthCode();
        companyAuthWithAuthCode(authCode);
    }

    /**
     * 变更授权
     *
     * @param decryptMsg
     */
    public void changeAuth(String decryptMsg) {
        log.info("wechat isv changeAuth, 开始处理企业更改授权");
        WeChatIsvCompanyChangeAuthDecryptBody weChatIsvCompanyAuthDecryptBody = (WeChatIsvCompanyChangeAuthDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvCompanyChangeAuthDecryptBody.class);
        WeChatIsvCompany qywxIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(weChatIsvCompanyAuthDecryptBody.getAuthCorpId());
        if (qywxIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        weChatIsvPullThirdOrgService.pullThirdOrg(qywxIsvCompany.getCorpId());
    }

    /**
     * 取消授权
     *
     * @param decryptMsg
     */
    public void cancelAuth(String decryptMsg) {
        log.info("wechat isv changeAuth, 开始处理企业取消授权");
        WeChatIsvCompanyChangeAuthDecryptBody weChatIsvCompanyAuthDecryptBody = (WeChatIsvCompanyChangeAuthDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvCompanyChangeAuthDecryptBody.class);
        WeChatIsvCompany qywxIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(weChatIsvCompanyAuthDecryptBody.getAuthCorpId());
        qywxIsvCompany.setState(CompanyAuthState.AUTH_CANCEL.getCode());
        if (qywxIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        weChatIsvCompanyDefinitionService.updateWechatIsvCompany(qywxIsvCompany);
    }

    /**
     * 获取第三方应用凭证
     *
     * @return
     */
    public String getSuiteAccessToken() {
        // 先尝试从redis查询
        String suiteAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_ISV_SUITE_ACCESS_TOKEN);
        String suiteAccessToken = (String) redisTemplate.opsForValue().get(suiteAccessTokenKey);
        if (!StringUtils.isBlank(suiteAccessToken)) {
            return suiteAccessToken;
        }
        // redis未命中， 重新获取
        String getSuiteTokenUrl = wechatHost + "/cgi-bin/service/get_suite_token";
        SuiteTokenRequest suiteTokenRequest = new SuiteTokenRequest();
        suiteTokenRequest.setSuiteId(suiteId);
        suiteTokenRequest.setSuiteSecret(suiteSecret);
        String suiteTicketKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_ISV_SUITE_TICKET);
        String suiteTicket = (String) redisTemplate.opsForValue().get(suiteTicketKey);
        if (StringUtils.isBlank(suiteTicket)) {
            log.info("wechat isv getSuiteAccessToken, suiteTicket获取失败");
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_SUITE_TICKET_IS_NULL));
        }
        suiteTokenRequest.setSuiteTicket(suiteTicket);
        String res = httpUtil.postJson(getSuiteTokenUrl, JsonUtils.toJson(suiteTokenRequest));
        log.info("wechat isv getSuiteAccessToken res is {}", res);
        SuiteTokenResponse suiteTokenResponse = JsonUtils.toObj(res, SuiteTokenResponse.class);
        if (suiteTokenResponse == null || StringUtils.isBlank(suiteTokenResponse.getSuiteAccessToken())) {
            log.info("wechat isv getSuiteAccessToken失败:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_SUITE_ACCESS_TOKEN_IS_NULL));
        }
        suiteAccessToken = suiteTokenResponse.getSuiteAccessToken();
        // 缓存redis
        log.info("wechat isv saveSuiteAccessToken,key={},value={}", suiteAccessTokenKey, suiteAccessToken);
        redisTemplate.opsForValue().set(suiteAccessTokenKey, suiteAccessToken);
        // 有效期7200秒，设置7000秒过期，防止不可用
        redisTemplate.expire(suiteTicketKey, 7000, TimeUnit.SECONDS);
        return suiteAccessToken;
    }

    /**
     * 清除suiteAccessToken，微信返回40082,invalid suite_acccess_token时清除redis，再重新获取
     */
    public void clearSuiteAccessToken() {
        String suiteAccessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_ISV_SUITE_ACCESS_TOKEN);
        redisTemplate.delete(suiteAccessTokenKey);
    }


    /**
     * 缓存SuiteTicket
     *
     * @param decryptMsg
     */
    public void saveSuiteTicket(String decryptMsg) {
        WeChatIsvSuiteTicketCallbackDecryptBody weChatIsvSuiteTicketCallbackDecryptBody = (WeChatIsvSuiteTicketCallbackDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvSuiteTicketCallbackDecryptBody.class);
        String suiteTicket = weChatIsvSuiteTicketCallbackDecryptBody.getSuiteTicket();
        String suiteTicketKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, WeChatRedisKeyConstant.WECHAT_ISV_SUITE_TICKET);
        log.info("wechat isv saveSuiteTicket,key={},value={}", suiteTicketKey, suiteTicket);
        redisTemplate.opsForValue().set(suiteTicketKey, suiteTicket);
        redisTemplate.expire(suiteTicketKey, 30, TimeUnit.MINUTES);
    }

    /**
     * 获取corpToken
     *
     * @param corpId
     * @return
     */
    public String getAccessTokenByCorpId(String corpId) {
        // 先尝试从redis查询
        String accessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_ISV_ACCESS_TOKEN, corpId));
        String accessToken = (String) redisTemplate.opsForValue().get(accessTokenKey);
        if (!StringUtils.isBlank(accessToken)) {
            return accessToken;
        }
        // 未命中缓存， 重新请求
        String getCorpTokenUrl = wechatHost + "/cgi-bin/service/get_corp_token?suite_access_token=";
        WeChatIsvCompany qywxIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        CorpTokenRequest corpTokenRequest = new CorpTokenRequest();
        corpTokenRequest.setAuthCorpid(corpId);
        corpTokenRequest.setPermanentCode(qywxIsvCompany.getPermanentCode());
        String res = wechatIsvHttpUtil.postJsonWithSuiteAccessToken(getCorpTokenUrl, JsonUtils.toJson(corpTokenRequest));
        log.info("wechat isv getCorpToken res is {}", res);
        CorpTokenResponse corpTokenResponse = JsonUtils.toObj(res, CorpTokenResponse.class);
        if (corpTokenResponse == null || StringUtils.isBlank(corpTokenResponse.getAccessToken())) {
            log.info("wechat isv getCorpToken失败:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ACCESS_TOKEN_IS_NULL));
        }
        accessToken = corpTokenResponse.getAccessToken();
        // 缓存redis
        log.info("wechat isv saveAccessToken,key={},value={}", accessTokenKey, accessToken);
        redisTemplate.opsForValue().set(accessTokenKey, accessToken);
        // 有效期7200秒，设置7000秒过期，防止不可用
        redisTemplate.expire(accessTokenKey, 7000, TimeUnit.SECONDS);
        return accessToken;
    }

    /**
     * 清除accessToken，微信返回42001,access_token expired时清除redis，再重新获取
     */
    public void clearAccessToken(String corpId) {
        String accessTokenKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_ISV_ACCESS_TOKEN, corpId));
        redisTemplate.delete(accessTokenKey);
    }

    /**
     * 获取企业授权信息
     *
     * @param corpId
     * @return
     */
    public AuthInfoResponse getAuthInfo(String corpId) {
        String getAuthInfoUrl = wechatHost + "/cgi-bin/service/get_auth_info?suite_access_token=";
        WeChatIsvCompany qywxIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
        AuthInfoRequest authInfoRequest = new AuthInfoRequest();
        authInfoRequest.setAuthCorpid(corpId);
        authInfoRequest.setPermanentCode(qywxIsvCompany.getPermanentCode());
        String res = wechatIsvHttpUtil.postJsonWithSuiteAccessToken(getAuthInfoUrl, JsonUtils.toJson(authInfoRequest));
        log.info("wechat isv getAuthInfo res is {}", res);
        AuthInfoResponse authInfoResponse = JsonUtils.toObj(res, AuthInfoResponse.class);
        if (authInfoResponse == null || authInfoResponse.getAuthInfo() == null) {
            log.info("wechat isv getAuthInfo失败:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ACCESS_TOKEN_IS_NULL));
        }
        return authInfoResponse;
    }

    /**
     * 获取永久授权码
     *
     * @param authCode
     * @return
     */
    private CompanyAuthResponse getPermanentCode(String authCode) {
        String getPermanentCodeUrl = wechatHost + "/cgi-bin/service/get_permanent_code?suite_access_token=";
        CompanyAuthRequest companyAuthRequest = new CompanyAuthRequest();
        companyAuthRequest.setAuthCode(authCode);
        String res = wechatIsvHttpUtil.postJsonWithSuiteAccessToken(getPermanentCodeUrl, JsonUtils.toJson(companyAuthRequest));
        log.info("wechat isv companyAuth, getPermanentCode res is {}", res);
        CompanyAuthResponse companyAuthResponse = JsonUtils.toObj(res, CompanyAuthResponse.class);
        if (companyAuthResponse == null || StringUtils.isBlank(companyAuthResponse.getPermanentCode())) {
            log.info("wechat isv companyAuth, getPermanentCode 失败:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_PERMANENT_IS_NULL));
        }
        return companyAuthResponse;
    }

    /**
     * 初始化企业授权信息
     *
     * @param companyAuthResponse
     */
    public WeChatIsvCompany initCompany(CompanyAuthResponse companyAuthResponse) {
        //1.查看企业是否授权过
        WeChatIsvCompany qywxIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(companyAuthResponse.getAuthCorpInfo().getCorpid());
        //2.授权过的企业，更新授权信息
        if (qywxIsvCompany != null) {
            qywxIsvCompany.setPermanentCode(companyAuthResponse.getPermanentCode());
            CompanyAuthResponse.Agent agent = companyAuthResponse.getAuthInfo().getAgent().get(0);
            qywxIsvCompany.setAgentid(agent.getAgentid());
            qywxIsvCompany.setThirdAdminId(companyAuthResponse.getAuthUserInfo().getUserid());
            qywxIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
            log.info("initCompany, 开始updateIsvCompany，corpid={}", companyAuthResponse.getAuthCorpInfo().getCorpid());
            weChatIsvCompanyDefinitionService.updateWechatIsvCompany(qywxIsvCompany);
        } else {
            //3.uc创建企业合同
            log.info("initCompany, 开始uc创建企业, corpid={}", companyAuthResponse.getAuthCorpInfo().getCorpid());
            CompanyCreatVo companyUC = createCompanyUC(companyAuthResponse, "");
            log.info("initCompany, 开始uc创建合同, corpid={}", companyAuthResponse.getAuthCorpInfo().getCorpid());
            //4.保存企业信息
            log.info("initCompany, 开始saveIsvCompany，corpid={}", companyAuthResponse.getAuthCorpInfo().getCorpid());
            qywxIsvCompany = saveIsvCompany(companyAuthResponse, companyUC);
            openCompanySourceTypeService.saveOpenCompanySourceType(companyUC.getCompanyId(), companyUC.getCompanyName(), companyAuthResponse.getAuthCorpInfo().getCorpid(), OpenType.WECHAT_ISV.getType());
        }
        return qywxIsvCompany;
    }


    /**
     * uc创建企业
     *
     * @param companyAuthResponse
     */
    public CompanyCreatVo createCompanyUC(CompanyAuthResponse companyAuthResponse, String companyNameSuffix) {
        CompanyCreatVo companyCreatVo = new CompanyCreatVo();
        CompanyAuthorizeDto companyAuth = new CompanyAuthorizeDto();
        companyAuth.setBirthDate(DateUtils.toDate("2020-01-01"));//授权负责人生日  默认2020-01-01
        String uuid = getUUId();
        companyAuth.setThirdUserId(companyAuthResponse.getAuthUserInfo().getUserid());
        companyAuth.setEmail(uuid + "@trial.com");//授权负责人邮箱 必填 默认trial@trial.com
        companyAuth.setIdNumber("110101190001011009");//授权负责人证件号 必填 默认虚拟值
        companyAuth.setIdType(EmployeeCert.IdCard.getKey());//授权负责人证件类型 必填
        companyAuth.setMobile(StringUtils.obj2str(virtualPhoneUtils.getVirtualPhone()));//授权负责人手机号 必填 默认虚拟值
        companyAuth.setName(companyAuthResponse.getAuthUserInfo().getName());//授权负责人名称 必填
        companyAuth.setSex(GenderType.FEMALE.getCode());//授权负责人性别 1 男 2 女 必填 默认女
        companyCreatVo.setThirdCompanyId(companyAuthResponse.getAuthCorpInfo().getCorpid());
        companyCreatVo.setCompanyAuth(companyAuth);//授权负责人信息
        companyCreatVo.setCompanySrc(CompanySourceType.WECHAT.getValue());//企业来源  必填
        companyCreatVo.setCompanyLogUrl(companyAuthResponse.getAuthCorpInfo().getCorpSquareLogoUrl());//企业头像地址
        companyCreatVo.setCompanyAddress("未设置");//联系地址  未设置
        companyCreatVo.setCompanyCode(uuid);//营业执照号   必填  随机
        String companyName = companyAuthResponse.getAuthCorpInfo().getCorpFullName();
        if (StringUtils.isBlank(companyName)) {
            companyName = companyAuthResponse.getAuthCorpInfo().getCorpName();
        }
        if ("未命名企业".equals(companyName)) {
            companyName = companyName + companyAuth.getMobile();
        }
        companyCreatVo.setCompanyName(companyName + companyNameSuffix);//企业名称  必填
        companyCreatVo.setCooperatingModel(FundAccountModelType.RECHARGE.getKey());//合作模式 1授信 2充值 必填, 默认充值模式。
        companyCreatVo.setBusinessAccountModel(FundAccountModelType.RECHARGE.getKey());
        companyCreatVo.setPersonalAccountModel(FundAccountModelType.RECHARGE.getKey());
        companyCreatVo.setCooperatingState(CompanyStatus.TRIAL.getKey());//合作状态 ，默认试用
        companyCreatVo.setCreateTime(DateUtils.toSimpleStr(DateUtils.now(), true));//准入日期 必填
        companyCreatVo.setEnterpriseLevel(3);//企业等级， 默认B
        companyCreatVo.setInitCredit(BigDecimal.ZERO);//授信额度 必填 默认0
        companyCreatVo.setOperateorId(1L);//操作人id 必填
        companyCreatVo.setOperateorMobile(getPhoneRandom());//操作人手机 必填
        companyCreatVo.setOperateorName(companyAuth.getName());//操作人名称 必填
        companyCreatVo.setPackageCode(PackageType.WECHAT_PACKAGE.getKey());//套餐id 必填 企业微信版
        companyCreatVo.setShortCompanyName(companyAuthResponse.getAuthCorpInfo().getCorpName() + companyNameSuffix);//企业简称
        CompanyCreatVo companyCreatResult = null;
        ContractVo contractVo = addOrUpdateContract(companyCreatVo);
        try {
            log.info("createCompanyUC companyCreatVo={}, contractVo={}", JsonUtils.toJson(companyCreatVo), JsonUtils.toJson(contractVo));
            companyCreatResult = iCompanyNewInfoService.createCompany(companyCreatVo, contractVo);
            log.info("createCompanyUC res={}", JsonUtils.toJson(companyCreatResult));
            companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
        } catch (FinhubException e) {
            log.error("createCompanyUC ,uc创建企业失败", e);
            if (e.getCode() == 20089) {
                String currentTimeMillis = StringUtils.obj2str(System.currentTimeMillis());
                companyCreatVo.setCompanyName( companyAuthResponse.getAuthCorpInfo().getCorpName() + "_" + currentTimeMillis);//企业名称  必填
                companyCreatVo.setShortCompanyName(companyAuthResponse.getAuthCorpInfo().getCorpName() + "_" + currentTimeMillis);//企业简称
                log.info("createCompanyUC ,uc创建企业名称重复，加后缀重试 companyCreatVo={}, contractVo={}", JsonUtils.toJson(companyCreatVo), JsonUtils.toJson(contractVo));
                companyCreatResult = iCompanyNewInfoService.createCompany(companyCreatVo, contractVo);
                log.info("createCompanyUC res={}", JsonUtils.toJson(companyCreatResult));
                companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
            } else {
                //exceptionRemind.exceptionRemindDingTalk(e);
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CREATE_COMPANY_FAILED), e.getMessage());
            }
        }
        log.info("createCompanyUC, 创建企业成功");
        return companyCreatVo;
    }


    /**
     * 新增企业合同
     *
     * @param companyCreatVo
     * @return
     */
    public ContractVo addOrUpdateContract(CompanyCreatVo companyCreatVo) {
        ContractVo contractVo = new ContractVo();
        contractVo.setCompanyId(companyCreatVo.getCompanyId());
        contractVo.setCompanyName(companyCreatVo.getCompanyName());
        contractVo.setCode(companyCreatVo.getCompanyAuth().getMobile());//合同code 虚拟值
        contractVo.setBeginDate(companyCreatVo.getCreateTime());//生效日期
        String trialDay = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WECHAT_ISV_TRIAL_DAY.getCode());
        if (StringUtils.isBlank(trialDay)) {
            trialDay = "30";
        }
        contractVo.setEndDate(DateUtils.afterDay(Integer.valueOf(trialDay)));//试用期30天
        contractVo.setRate(BigDecimal.ZERO);//费率默认0
        contractVo.setBillDay(BillDayType.ONE.getValue());//账单日 默认1
        contractVo.setRepaymentDay(RepaymentDayEmnum.ONE_THIRD_KIND.getRepaymentDay());//还款日
        contractVo.setPlatformFee(BigDecimal.ZERO);//平台服务费 默认0
        contractVo.setCooperatingModel(companyCreatVo.getCooperatingModel());//合作模式 充值
        contractVo.setPaymentDays(PaymentDayType.FIRST_KIND.getKey());//账期 默认30+15
        contractVo.setRenew(false);
        //log.info("addOrUpdateContract req={}", contractVo);
        //ContractVo contractVoRes = iCompanyNewInfoService.addOrUpdateContract(contractVo);
        //log.info("addOrUpdateContract res={}", contractVoRes);
        //log.info("addOrUpdateContract, 新增合同成功");
        return contractVo;
    }

    /**
     * 保存企业授权信息
     *
     * @param companyAuthResponse
     */
    private WeChatIsvCompany saveIsvCompany(CompanyAuthResponse companyAuthResponse, CompanyCreatVo companyUC) {
        WeChatIsvCompany weChatIsvCompany = new WeChatIsvCompany();
        weChatIsvCompany.setCompanyId(companyUC.getCompanyId());
        weChatIsvCompany.setCorpId(companyAuthResponse.getAuthCorpInfo().getCorpid());
        weChatIsvCompany.setCompanyName(companyUC.getCompanyName());
        weChatIsvCompany.setPermanentCode(companyAuthResponse.getPermanentCode());
        CompanyAuthResponse.Agent agent = companyAuthResponse.getAuthInfo().getAgent().get(0);
        weChatIsvCompany.setAgentid(agent.getAgentid());
        weChatIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
        weChatIsvCompany.setThirdAdminId(companyAuthResponse.getAuthUserInfo().getUserid());
        weChatIsvCompanyDefinitionService.saveWeChatIsvCompany(weChatIsvCompany);
        return weChatIsvCompany;
    }

    /**
     * 获取18位随机数
     *
     * @return
     */
    public static String getUUId() {
        int first = new Random(10).nextInt(8) + 1;
        System.out.println(first);
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // d 代表参数为正数型
        return first + String.format("%017d", hashCodeV);
    }

    /**
     * 随机手机号
     *
     * @return
     */
    public static String getPhoneRandom() {
        String phone = "130";
        int number = (int) ((Math.random() * 9 + 1) * 10000000);
        return phone + number;
    }

    /**
     * 付费版本变更通知
     *
     * @param corpId
     */
    public void companyChangeEditon(String corpId) {
        log.info("wechat isv companyChangeEditon, 开始处理付费版本变更通知");
        String lockKey = MessageFormat.format(RedisKeyConstant.CHANGE_CONTRACT, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 30 * 60 * 1000L);
        if (lockTime > 0) {
            try {
                AuthInfoResponse authInfo = getAuthInfo(corpId);
                AuthInfoResponse.EditionAgent agent = authInfo.getEditionInfo().getAgent().get(0);
                Long expiredTime = agent.getExpiredTime();
                if (expiredTime != null) {
                    WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
                    if (weChatIsvCompany == null) {
                        throw new OpenApiWechatException(NumericUtils.obj2int(WECHAT_ISV_COMPANY_UNDEFINED));
                    }
                    Date endDate = DateUtils.toDate(NumericUtils.obj2long(expiredTime * 1000));
                    ContractInfoDTO contractInfoDTO = new ContractInfoDTO();
                    contractInfoDTO.setCompanyId(weChatIsvCompany.getCompanyId());
                    contractInfoDTO.setEndDate(endDate);
                    iCompanyNewInfoService.updateCompanyContractInfo(contractInfoDTO);
                }
            } catch (Exception e) {
                log.error("处理付费版本变更失败, result: {}", e);
                throw e;
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        }
    }


    /**
     * 获取临时授权码
     */
    public String getPreAuthCode() {
        String getAuthInfoUrl = wechatHost + "/cgi-bin/service/get_pre_auth_code?suite_access_token={suite_access_token}";
        String res = wechatIsvHttpUtil.getJsonWithSuiteAccessToken(getAuthInfoUrl, new HashMap<>());
        WeChatIsvGetPreAutAuthResponse weChatIsvGetPreAutAuthResponse = JsonUtils.toObj(res, WeChatIsvGetPreAutAuthResponse.class);
        if (weChatIsvGetPreAutAuthResponse == null || weChatIsvGetPreAutAuthResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ERROR));
        }
        String preAuthCode = weChatIsvGetPreAutAuthResponse.getPreAuthCode();
        return preAuthCode;
    }

    /**
     * 设置授权配置
     *
     * @param preAuthCode
     */
    public void setSessionInfo(String preAuthCode) {
        String setSessionInfoUrl = wechatHost + "/cgi-bin/service/set_session_info?suite_access_token=";
        WeChatIsvSetSessionInfoRequest weChatIsvSetSessionInfoRequest = new WeChatIsvSetSessionInfoRequest();
        weChatIsvSetSessionInfoRequest.setPreAuthCode(preAuthCode);
        WeChatIsvSetSessionInfoRequest.SessionInfo sessionInfo = new WeChatIsvSetSessionInfoRequest.SessionInfo();
        sessionInfo.setAuthType(NumericUtils.obj2int(authtype));
        weChatIsvSetSessionInfoRequest.setSessionInfo(sessionInfo);
        String res = wechatIsvHttpUtil.postJsonWithSuiteAccessToken(setSessionInfoUrl, JsonUtils.toJson(weChatIsvSetSessionInfoRequest));
        WeChatIsvGetPreAutAuthResponse weChatIsvGetPreAutAuthResponse = JsonUtils.toObj(res, WeChatIsvGetPreAutAuthResponse.class);
        if (weChatIsvGetPreAutAuthResponse == null || weChatIsvGetPreAutAuthResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ERROR));
        }
    }

    /**
     * 企业授权,返回授权人三方id
     *
     * @param authCode
     */
    public Map<String, String> companyAuthWithAuthCode(String authCode) {
        Map<String, String> result = new HashMap<>();
        log.info("wechat isv companyAuth, 开始处理企业授权");
        // 1.获取企业永久授权码
        CompanyAuthResponse companyAuthResponse = getPermanentCode(authCode);
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, companyAuthResponse.getAuthCorpInfo().getCorpid());
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 30 * 60 * 1000L);
        if (lockTime > 0) {
            try {
                // 2.初始化企业授权信息
                WeChatIsvCompany weChatIsvCompany = initCompany(companyAuthResponse);
                // 3.同步组织人员数据
                weChatIsvPullThirdOrgService.pullThirdOrg(companyAuthResponse.getAuthCorpInfo().getCorpid());
                String thirdEmployeeId = companyAuthResponse.getAuthUserInfo().getUserid();
                result.put("companyId", weChatIsvCompany.getCompanyId());
                result.put("thirdEmployeeId", thirdEmployeeId);
                weChatIsvMessageService.sendInstallSuccessMessage(thirdEmployeeId, weChatIsvCompany.getAgentid(), weChatIsvCompany.getCorpId());
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        }
        return result;
    }


    /**
     * @param authCode
     */
    public WeChatIsvWebLoginInfo installAuth(String authCode) {
        Map<String, String> map = companyAuthWithAuthCode(authCode);
        String companyId = map.get("companyId");
        String thirdUserId = map.get("thirdEmployeeId");
        // 授权登录，并返回信息
        return weChatIsvUserAuthService.webLogin(companyId, thirdUserId);
    }


    /**
     * 获取临时授权码
     */
    public String getRegisterCode() {
        String getRegisterCodeUrl = wechatHost + "/cgi-bin/service/get_register_code?provider_access_token=";
        WeChatIsvGetRegisterCodeRequest weChatIsvGetRegisterCodeRequest = new WeChatIsvGetRegisterCodeRequest();
        weChatIsvGetRegisterCodeRequest.setTemplateId(templateId);
        String res = wechatIsvHttpUtil.postJsonWithProviderAccessToken(getRegisterCodeUrl, JsonUtils.toJson(weChatIsvGetRegisterCodeRequest));
        WeChatIsvGetRegisterCodeResponse weChatIsvGetRegisterCodeResponse = JsonUtils.toObj(res, WeChatIsvGetRegisterCodeResponse.class);
        if (weChatIsvGetRegisterCodeResponse == null || weChatIsvGetRegisterCodeResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ERROR));
        }
        String registerCode = weChatIsvGetRegisterCodeResponse.getRegisterCode();
        return registerCode;
    }
}
