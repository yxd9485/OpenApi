package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.fenbeitong.finhub.common.constant.FundAccountModelType;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.CacheConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkIsvConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvMarketOrderDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvTryOutDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dto.OpenSysConfigReqDTO;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.privilege.service.OpenEmployeePrivService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.PhoneGenUtils;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyAuthorizeDto;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractInfoDTO;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractVo;
import com.fenbeitong.usercenter.api.model.enums.company.*;
import com.fenbeitong.usercenter.api.model.enums.employee.EmployeeCert;
import com.fenbeitong.usercenter.api.model.enums.employee.GenderType;
import com.fenbeitong.usercenter.api.model.enums.function.PackageType;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import com.fenbeitong.usercenter.api.service.company.IRCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lizhen
 * @date 2020/7/10
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvCompanyAuthServiceImpl implements IDingtalkIsvCompanyAuthService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Value("${dingtalk.isv.suitekey}")
    private String suiteKey;

    @Value("${dingtalk.isv.suiteSecret}")
    private String suiteSecret;

    @Value("${dingtalk.isv.corpId}")
    private String isvCorpId;

    @Value("${dingtalk.isv.SSOsecret}")
    private String SSOsecret;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @DubboReference(check = false)
    private ICompanyNewInfoService iCompanyNewInfoService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private OpenEmployeePrivService openEmployeePrivService;

    @DubboReference(check = false)
    private IRCompanyService irCompanyService;

    @Autowired
    private OpenSyncBizDataDao openSyncBizDataDao;

    /**
     * 企业授权
     *
     * @param corpId
     */
    @Override
    public void companyAuth(String corpId) {
        log.info("【dingtalk isv】 companyAuth, 开始处理企业授权");
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                // 1.初始化企业授权信息
                String companyId = initCompany(corpId);
                String initRoleTypeFlag = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE_FLAG.getCode());
                if ("true".equals(initRoleTypeFlag)) {
                    // 初始化权限
                    openEmployeePrivService.initRoleType("1", OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE.getCode(), companyId);
                }
                // 2.同步组织人员数据
                dingtalkIsvEmployeeService.syncOrgEmployee(corpId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【dingtalk isv】 companyAuth, 未获取到锁，corpId={}", corpId);
        }
    }


    /**
     * 初始化企业授权信息
     *
     * @return
     */
    public String initCompany(String corpId) {
        // 1.获取企业永久授权码
//        if (!StringUtils.isBlank(tmpAuthCode)) {
//            OapiServiceGetPermanentCodeResponse companyPermanentInfo = getPermanentCode(tmpAuthCode);
//            permanentCode = companyPermanentInfo.getPermanentCode();
//        }
        //2.查看企业是否授权过
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        //企业授权信息
        OapiServiceGetAuthInfoResponse companyAuthInfo = getAuthInfo(corpId);
        //3.授权过的企业，更新授权信息
        if (dingtalkIsvCompany != null) {
            //dingtalkIsvCompany.setPermanentCode(permanentCode);
            OapiServiceGetAuthInfoResponse.Agent agent = companyAuthInfo.getAuthInfo().getAgent().get(0);
            dingtalkIsvCompany.setAgentid(agent.getAgentid());
            dingtalkIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
            dingtalkIsvCompany.setThirdAdminId(companyAuthInfo.getAuthUserInfo().getUserId());
            log.info("【dingtalk isv】initCompany, 开始updateIsvCompany，corpid={}", corpId);
            dingtalkIsvCompanyDefinitionService.updateDingtalkIsvCompany(dingtalkIsvCompany);
        } else {
            //4.uc创建企业合同
            log.info("【dingtalk isv】initCompany, 开始uc创建企业, corpid={}", corpId);
            //授权用户信息
            OapiUserGetResponse userInfo = dingtalkIsvEmployeeService.getUserInfo(companyAuthInfo.getAuthUserInfo().getUserId(), corpId);
            CompanyCreatVo companyUC = createCompanyUC(companyAuthInfo, userInfo, "");
            //3.保存企业信息
            log.info("【dingtalk isv】initCompany, 开始saveIsvCompany，corpid={}", corpId);
            dingtalkIsvCompany = saveIsvCompany(companyUC, companyAuthInfo, null);
            openCompanySourceTypeService.saveOpenCompanySourceType(companyUC.getCompanyId(), companyUC.getCompanyName(), corpId, OpenType.DINGTALK_ISV.getType());
//            log.info("【dingtalk isv】initCompany, 开始uc创建合同, corpid={}", corpId);
//            addOrUpdateContract(companyUC);
        }
        return dingtalkIsvCompany.getCompanyId();
    }

    @Override
    public void updateCompanyAuth(String corpId) {
        log.info("【dingtalk isv】 companyAuth, 开始处理企业授权变更");
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                // 1.更新企业授权信息
                updateAuth(corpId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【dingtalk isv】 companyAuth, 未获取到锁，corpId={}", corpId);
        }
    }

    /**
     * 变更授权信息
     *
     * @return
     */
    public void updateAuth(String corpId) {
        //2.查看企业是否授权过
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED));
        }
        //企业授权信息
        OapiServiceGetAuthInfoResponse companyAuthInfo = getAuthInfo(corpId);
        String mainCorpId = dingtalkIsvCompany.getMainCorpId();
        OapiServiceGetAuthInfoResponse.Agent agent = companyAuthInfo.getAuthInfo().getAgent().get(0);
        dingtalkIsvCompany.setAgentid(agent.getAgentid());
        dingtalkIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
        dingtalkIsvCompany.setThirdAdminId(companyAuthInfo.getAuthUserInfo().getUserId());
        log.info("【dingtalk isv】updateAuth, 开始updateIsvCompany，corpid={}", corpId);
        dingtalkIsvCompanyDefinitionService.updateDingtalkIsvCompany(dingtalkIsvCompany);
        //企业变更需同步人员信息
        if (StringUtils.isBlank(mainCorpId)) {
            String initRoleTypeFlag = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE_FLAG.getCode());
            if ("true".equals(initRoleTypeFlag)) {
                // 初始化权限
                openEmployeePrivService.initRoleType("1", OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE.getCode(), dingtalkIsvCompany.getCompanyId());
            }
            // 2.同步组织人员数据
            dingtalkIsvEmployeeService.syncOrgEmployee(corpId);
        }
    }


    /**
     * uc创建企业
     *
     * @param companyAuthInfo
     * @param userInfo
     * @param companyNameSuffix
     * @return
     */
    public CompanyCreatVo createCompanyUC(OapiServiceGetAuthInfoResponse companyAuthInfo, OapiUserGetResponse userInfo, String companyNameSuffix) {
        CompanyCreatVo companyCreatVo = new CompanyCreatVo();
        CompanyAuthorizeDto companyAuth = new CompanyAuthorizeDto();
        companyAuth.setBirthDate(DateUtils.toDate("2020-01-01"));//授权负责人生日  默认2020-01-01
        String uuid = getUUId();
        companyAuth.setThirdUserId(userInfo.getUserid());
        companyAuth.setName(userInfo.getName());//授权负责人名称 必填
        if (StringUtils.isBlank(userInfo.getEmail())) {
            companyAuth.setEmail(uuid + "@trial.com");//授权负责人邮箱 必填 默认trial@trial.com
        } else {
            companyAuth.setEmail(userInfo.getEmail());
        }
        companyAuth.setIdNumber("110101190001011009");//授权负责人证件号 必填 默认虚拟值
        companyAuth.setIdType(EmployeeCert.IdCard.getKey());//授权负责人证件类型 必填
        companyAuth.setMobile(StringUtils.obj2str(virtualPhoneUtils.getVirtualPhone()));//授权负责人手机号 必填 默认虚拟值
        companyAuth.setSex(GenderType.FEMALE.getCode());//授权负责人性别 1 男 2 女 必填 默认女
        companyCreatVo.setThirdCompanyId(companyAuthInfo.getAuthCorpInfo().getCorpid());
        companyCreatVo.setCompanyAuth(companyAuth);//授权负责人信息
        companyCreatVo.setCompanySrc(CompanySourceType.DING_TALK.getValue());//企业来源  必填
        companyCreatVo.setCompanyAddress("未设置");//联系地址  未设置
        companyCreatVo.setCompanyCode(uuid);//营业执照号   必填  随机
        companyCreatVo.setCompanyName(companyAuthInfo.getAuthCorpInfo().getCorpName() + companyNameSuffix);//企业名称  必填
        companyCreatVo.setCooperatingModel(FundAccountModelType.RECHARGE.getKey());//合作模式 1授信 2充值 必填, 默认充值模式。
        companyCreatVo.setBusinessAccountModel(FundAccountModelType.RECHARGE.getKey());
        companyCreatVo.setPersonalAccountModel(FundAccountModelType.RECHARGE.getKey());
        companyCreatVo.setCooperatingState(CompanyStatus.TRIAL.getKey());//合作状态 ，默认试用
        companyCreatVo.setCreateTime(DateUtils.toSimpleStr(DateUtils.now(), true));//准入日期 必填
        companyCreatVo.setEnterpriseLevel(3);//企业等级， 默认B
        companyCreatVo.setInitCredit(BigDecimal.ZERO);//授信额度 必填 默认0
        companyCreatVo.setOperateorId(1L);//操作人id 必填
        companyCreatVo.setOperateorMobile(PhoneGenUtils.getPhoneRandom());//操作人手机 必填
        companyCreatVo.setOperateorName(companyAuth.getName());//操作人名称 必填
        companyCreatVo.setPackageCode(PackageType.DING_TALK.getKey());//套餐id 必填 企业微信版
        companyCreatVo.setShortCompanyName(companyAuthInfo.getAuthCorpInfo().getCorpName() + companyNameSuffix);//企业简称
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
                companyCreatVo.setCompanyName(companyAuthInfo.getAuthCorpInfo().getCorpName() + "_" + currentTimeMillis);//企业名称  必填
                companyCreatVo.setShortCompanyName(companyAuthInfo.getAuthCorpInfo().getCorpName() + "_" + currentTimeMillis);//企业简称
                log.info("createCompanyUC ,uc创建企业名称重复，加后缀重试 companyCreatVo={}, contractVo={}", JsonUtils.toJson(companyCreatVo), JsonUtils.toJson(contractVo));
                companyCreatResult = iCompanyNewInfoService.createCompany(companyCreatVo, contractVo);
                log.info("createCompanyUC res={}", JsonUtils.toJson(companyCreatResult));
                companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
            } else {
                //exceptionRemind.exceptionRemindDingTalk(e);
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_CREATE_COMPANY_FAILED, e.getMessage());
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
        String trialDay = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.DINGTALK_ISV_TRIAL_DAY.getCode());
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
//        log.info("addOrUpdateContract req={}", contractVo);
//        ContractVo contractVoRes = iCompanyNewInfoService.addOrUpdateContract(contractVo);
//        log.info("addOrUpdateContract res={}", contractVoRes);
//        log.info("addOrUpdateContract, 新增合同成功");
        return contractVo;
    }


    /**
     * 保存企业授权信息（企业）
     */
    /**
     * 保存企业授权信息
     *
     * @param companyAuthResponse
     */
    private DingtalkIsvCompany saveIsvCompany(CompanyCreatVo companyUC, OapiServiceGetAuthInfoResponse companyAuthResponse, String permanentCode) {
        DingtalkIsvCompany dingtalkIsvCompany = new DingtalkIsvCompany();
        dingtalkIsvCompany.setCompanyId(companyUC.getCompanyId());
        dingtalkIsvCompany.setCorpId(companyAuthResponse.getAuthCorpInfo().getCorpid());
        dingtalkIsvCompany.setCompanyName(companyUC.getCompanyName());
        dingtalkIsvCompany.setPermanentCode(permanentCode);
        OapiServiceGetAuthInfoResponse.Agent agent = companyAuthResponse.getAuthInfo().getAgent().get(0);
        dingtalkIsvCompany.setAgentid(agent.getAgentid());
        dingtalkIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
        dingtalkIsvCompany.setThirdAdminId(companyAuthResponse.getAuthUserInfo().getUserId());
        dingtalkIsvCompanyDefinitionService.saveDingtalkIsvCompany(dingtalkIsvCompany);
        return dingtalkIsvCompany;
    }

    @Override
    public void saveSuiteTicket(String suiteTicket) {
        String suiteTicketKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SUITE_TICKET);
        log.info("【dingtalk isv】 saveSuiteTicket,key={},value={}", suiteTicketKey, suiteTicket);
        redisTemplate.opsForValue().set(suiteTicketKey, suiteTicket);
        redisTemplate.expire(suiteTicketKey, 1, TimeUnit.DAYS);
    }

    @Override
    public String getSuiteTicket() {
        String suiteTicketKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SUITE_TICKET);
        String suiteTicket = (String) redisTemplate.opsForValue().get(suiteTicketKey);
        if (StringUtils.isBlank(suiteTicket)) {
            suiteTicket = openSyncBizDataDao.getSuiteTicket();
            redisTemplate.opsForValue().set(suiteTicketKey, suiteTicket);
            redisTemplate.expire(suiteTicketKey, 10, TimeUnit.MINUTES);
            if (StringUtils.isBlank(suiteTicket)) {
                log.info("【dingtalk isv】 , suiteTicket获取失败");
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_SUITE_TICKET_IS_NULL);
            }
        }
        return suiteTicket;
    }


    /**
     * 获取第三方应用凭证
     *
     * @return
     */
    @Override
    public String getSuiteAccessToken() {
        // 先尝试从redis查询
        String suiteAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SUITE_ACCESS_TOKEN);
        String suiteAccessToken = (String) redisTemplate.opsForValue().get(suiteAccessTokenKey);
        if (!StringUtils.isBlank(suiteAccessToken)) {
            return suiteAccessToken;
        }
        // redis未命中， 重新获取
//        String suiteTicketKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SUITE_TICKET);
//        String suiteTicket = (String) redisTemplate.opsForValue().get(suiteTicketKey);
//        if (StringUtils.isBlank(suiteTicket)) {
//            log.error("【dingtalk isv】 getSuiteAccessToken, suiteTicket获取失败");
//            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_SUITE_TICKET_IS_NULL);
//        }
        String suiteTicket = getSuiteTicket();
        String url = dingtalkHost + "service/get_suite_token";
        OapiServiceGetSuiteTokenRequest request = new OapiServiceGetSuiteTokenRequest();
        request.setSuiteKey(suiteKey);
        request.setSuiteSecret(suiteSecret);
        request.setSuiteTicket(suiteTicket);
        OapiServiceGetSuiteTokenResponse response = dingtalkIsvClientUtils.execute(url, request);
        suiteAccessToken = response.getSuiteAccessToken();
        // 缓存redis
        log.info("【dingtalk isv】 saveSuiteAccessToken,key={},value={}", suiteAccessTokenKey, suiteAccessToken);
        redisTemplate.opsForValue().set(suiteAccessTokenKey, suiteAccessToken);
        redisTemplate.expire(suiteAccessTokenKey, response.getExpiresIn(), TimeUnit.SECONDS);
        return suiteAccessToken;
    }

    /**
     * corp_access_token
     *
     * @param corpId
     * @returnT
     */
    @Override
    public String getCorpAccessTokenByCorpId(String corpId) {
        // 先尝试从redis查询
        String corpAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(CacheConstant.DINGTALK_ISV_CORP_ACCESS_TOKEN, corpId));
        String corpAccessToken = (String) redisTemplate.opsForValue().get(corpAccessTokenKey);
        if (!StringUtils.isBlank(corpAccessToken)) {
            return corpAccessToken;
        }
        // 未命中缓存， 重新请求
        String url = dingtalkHost + "service/get_corp_token";
        OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
        req.setAuthCorpid(corpId);
        OapiServiceGetCorpTokenResponse oapiServiceGetCorpTokenResponse = dingtalkIsvClientUtils.executeWithSuiteInfo(url, req);
        corpAccessToken = oapiServiceGetCorpTokenResponse.getAccessToken();
        // 缓存redis
        log.info("【dingtalk isv】 saveCorpAccessToken,key={},value={}", corpAccessTokenKey, corpAccessToken);
        redisTemplate.opsForValue().set(corpAccessTokenKey, corpAccessToken);
        redisTemplate.expire(corpAccessTokenKey, oapiServiceGetCorpTokenResponse.getExpiresIn(), TimeUnit.SECONDS);
        return corpAccessToken;
    }

    /**
     * suiteAccessToken失效，清除redis重新获取
     */
    @Override
    public void clearSuiteAccessToken() {
        String suiteAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SUITE_ACCESS_TOKEN);
        redisTemplate.delete(suiteAccessTokenKey);
    }

    /**
     * corpAccessToken失效，清除redis重新获取
     *
     * @param corpId
     */
    @Override
    public void clearCorpAccessToken(String corpId) {
        String corpAccessTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(CacheConstant.DINGTALK_ISV_CORP_ACCESS_TOKEN, corpId));
        redisTemplate.delete(corpAccessTokenKey);
    }

    /**
     * 获取企业永久授权码
     *
     * @return
     */
    public OapiServiceGetPermanentCodeResponse getPermanentCode(String tmpAuthCode) {
        String url = dingtalkHost + "service/get_permanent_code?suite_access_token=";
        OapiServiceGetPermanentCodeRequest req = new OapiServiceGetPermanentCodeRequest();
        req.setTmpAuthCode(tmpAuthCode);
        OapiServiceGetPermanentCodeResponse rsp = dingtalkIsvClientUtils.executeWithSuiteAccessToken(url, req);
        return rsp;
    }

    /**
     * 激活应用
     *
     * @param corpId
     */
    @Override
    public void activateSuite(String corpId) {
        String url = dingtalkHost + "service/activate_suite?suite_access_token=";
        OapiServiceActivateSuiteRequest req = new OapiServiceActivateSuiteRequest();
        req.setSuiteKey(suiteKey);
        req.setAuthCorpid(corpId);
        //req.setPermanentCode(dingtalkIsvCompany.getPermanentCode());
        dingtalkIsvClientUtils.executeWithSuiteAccessToken(url, req);
    }

    /**
     * 获取企业授权信息
     *
     * @param corpId
     * @return
     */
    public OapiServiceGetAuthInfoResponse getAuthInfo(String corpId) {
        String url = dingtalkHost + "service/get_auth_info";
        OapiServiceGetAuthInfoRequest req = new OapiServiceGetAuthInfoRequest();
        req.setAuthCorpid(corpId);
        OapiServiceGetAuthInfoResponse oapiServiceGetAuthInfoResponse = dingtalkIsvClientUtils.executeWithSuiteInfo(url, req);
        return oapiServiceGetAuthInfoResponse;
    }

    /**
     * 获取18位随机数
     *
     * @return
     */
    private static String getUUId() {
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
     * 取消授权
     *
     * @param corpId
     */
    @Override
    public void companyCancelAuth(String corpId) {
        log.info("【dingtalk isv] cancelAuth, 开始处理企业取消授权");
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED));
        }
        dingtalkIsvCompany.setState(CompanyAuthState.AUTH_CANCEL.getCode());
        dingtalkIsvCompanyDefinitionService.updateDingtalkIsvCompany(dingtalkIsvCompany);
    }

    @Override
    public String getSSOToken() {
        // 先尝试从redis查询
        String SSOTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SSO_SECRET);
        String SSOToken = (String) redisTemplate.opsForValue().get(SSOTokenKey);
        if (!StringUtils.isBlank(SSOToken)) {
            return SSOToken;
        }
        // 未命中缓存， 重新请求
        String url = dingtalkHost + "sso/gettoken";
        OapiSsoGettokenRequest request = new OapiSsoGettokenRequest();
        request.setCorpid(isvCorpId);
        request.setCorpsecret(SSOsecret);
        request.setHttpMethod("GET");
        OapiSsoGettokenResponse execute = dingtalkIsvClientUtils.execute(url, request);
        SSOToken = execute.getAccessToken();
        // 缓存redis
        log.info("【dingtalk isv】 saveSSOToken,key={},value={}", SSOTokenKey, SSOToken);
        redisTemplate.opsForValue().set(SSOTokenKey, SSOToken);
        redisTemplate.expire(SSOTokenKey, 7000, TimeUnit.SECONDS);
        return SSOToken;
    }

    @Override
    public void clearSSOToken() {
        String SSOTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SSO_SECRET);
        redisTemplate.delete(SSOTokenKey);
    }


    /**
     * 付费版本变更通知
     *
     * @param dingtalkIsvMarketOrderDTO
     */
    @Override
    public void companyChangeEditon(DingtalkIsvMarketOrderDTO dingtalkIsvMarketOrderDTO) {
        log.info("dingtalk isv, 处理付费版本变更");
        String corpId = dingtalkIsvMarketOrderDTO.getCorpId();
        Long expiredTime = dingtalkIsvMarketOrderDTO.getServiceStopTime();
        if (expiredTime != null) {
            Date endDate = DateUtils.toDate(NumericUtils.obj2long(expiredTime));
            updateCompanyEndDate(corpId, endDate);
        }
    }

    /**
     * 个人或者企业授权
     *
     * @param dingtalkIsvMarketOrderDTO
     */
    @Override
    public void authCompanyOrPerson(DingtalkIsvMarketOrderDTO dingtalkIsvMarketOrderDTO) {
        log.info("【dingtalk isv】 authCompanyOrPerson, 开始处理企业授权");
        String corpId = dingtalkIsvMarketOrderDTO.getCorpId();
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                initAuthCompanyorPerson(dingtalkIsvMarketOrderDTO);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【dingtalk isv】 authCompanyOrPerson, 未获取到锁，corpId={}", corpId);
        }
    }

    public void initAuthCompanyorPerson(DingtalkIsvMarketOrderDTO dingtalkIsvMarketOrderDTO) {
        String corpId = dingtalkIsvMarketOrderDTO.getCorpId();
        String mainCorpId = dingtalkIsvMarketOrderDTO.getMainCorpId();
        //通过corpId查询dingtalk_isv_company中是否有数据
        if (StringUtils.isBlank(mainCorpId)) {
            String companyId = createAuthAndContractCompany(corpId);
            String initRoleTypeFlag = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE_FLAG.getCode());
            // 默认打开同步部门主管
            openSysConfigService.saveOpenSysConfigNotExce(
                    OpenSysConfigReqDTO.builder()
                            .type(OpenSysConfigType.OPEN_SET_DEP_MANAGER.getType())
                            .code(companyId)
                            .name("钉钉市场版开放部门主管同步")
                            .value("1")
                            .state(1)
                            .build());
            if ("true".equals(initRoleTypeFlag)) {
                // 初始化权限
                openEmployeePrivService.initRoleType("1", OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE.getCode(), companyId);
            }
            // 2.同步组织人员数据
            dingtalkIsvEmployeeService.syncOrgEmployee(corpId);
        } else {
            //个人 新增isv_company数据
            createAuthPerson(corpId, mainCorpId);
        }
    }

    /**
     * 新增或修改授权数据（企业）
     *
     * @param corpId
     * @return
     */
    public String createAuthAndContractCompany(String corpId) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            //新增
            //企业授权信息
            OapiServiceGetAuthInfoResponse companyAuthInfo = getAuthInfo(corpId);
            //uc创建企业合同
            log.info("【dingtalk isv】createAuthAndContractCompany, 开始uc创建企业和合同, corpid={}", corpId);
            //授权用户信息
            OapiUserGetResponse userInfo = dingtalkIsvEmployeeService.getUserInfo(companyAuthInfo.getAuthUserInfo().getUserId(), corpId);
            CompanyCreatVo companyUC = createCompanyUC(companyAuthInfo, userInfo, "");
            //3.保存企业信息
            log.info("【dingtalk isv】createAuthAndContractCompany, 开始saveIsvCompany，corpid={}", corpId);
            DingtalkIsvCompany dc = saveIsvCompany(companyUC, companyAuthInfo, null);
            openCompanySourceTypeService.saveOpenCompanySourceType(companyUC.getCompanyId(), companyUC.getCompanyName(), corpId, OpenType.DINGTALK_ISV.getType());
            return dc.getCompanyId();
        } else {
            //修改isv_company表中的状态数据
            log.info("【dingtalk isv】createAuthAndContractCompany, 开始updateIsvCompany，corpid={}", corpId);
            dingtalkIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
            dingtalkIsvCompanyDefinitionService.updateDingtalkIsvCompany(dingtalkIsvCompany);
            return dingtalkIsvCompany.getCompanyId();
        }
    }

    /**
     * 新增授权和合同数据（个人）
     *
     * @param corpId
     * @return
     */
    public String createAuthPerson(String corpId, String mainCorpId) {
        //判断是否存在，如果存在修改状态
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            //企业授权信息
//            OapiServiceGetAuthInfoResponse companyAuthInfo = getAuthInfo(corpId);
            //新增isv_company表
            log.info("【dingtalk isv】createAuthPerson, 开始新增授权信息, corpid={}", corpId);
            DingtalkIsvCompany dc = saveIsvPerson(corpId, mainCorpId);
            openCompanySourceTypeService.saveOpenCompanySourceType(dc.getCompanyId(), dc.getCompanyName(), corpId, OpenType.DINGTALK_ISV.getType());
            return dc.getCompanyId();
        } else {
            //修改isv_company表中的状态数据
            log.info("【dingtalk isv】createAuthPerson, 开始updateIsvCompany，corpid={}", corpId);
            dingtalkIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
            dingtalkIsvCompany.setMainCorpId(mainCorpId);
            dingtalkIsvCompanyDefinitionService.updateDingtalkIsvCompany(dingtalkIsvCompany);
            return dingtalkIsvCompany.getCompanyId();
        }
    }

    /**
     * 保存个人用户的企业授权信息（个人试用）
     *
     * @param corpId
     * @return
     */
    private DingtalkIsvCompany saveIsvPerson(String corpId, String mainCorpId) {
        DingtalkIsvCompany dingtalkIsvCompany = new DingtalkIsvCompany();
        String openSysConfigByCode = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.DINGTAlK_ISV_COMPONENT.getCode());
        if (StringUtils.isBlank(openSysConfigByCode))
            throw new OpenApiDingtalkException(DingtalkResponseCode.PERSON_COMPANY_UNCONFIGURATION);
        Map<String, Object> map = JsonUtils.toObj(openSysConfigByCode, Map.class);
        String companyId = (String) map.get("company_id");
        if (StringUtils.isBlank(companyId))
            throw new OpenApiDingtalkException(DingtalkResponseCode.PERSON_COMPANY_UNCONFIGURATION);
        dingtalkIsvCompany.setCompanyId(companyId);//公司id固定值(从open_sys_config中查询)
        dingtalkIsvCompany.setCorpId(corpId);//三方企业id
        dingtalkIsvCompany.setMainCorpId(mainCorpId);
        dingtalkIsvCompany.setCompanyName("个人试用企业");
//        OapiServiceGetAuthInfoResponse.Agent agent = companyAuthResponse.getAuthInfo().getAgent().get(0);
//        dingtalkIsvCompany.setAgentid(agent.getAgentid());
        dingtalkIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
//        dingtalkIsvCompany.setThirdAdminId(companyAuthResponse.getAuthUserInfo().getUserId());
        dingtalkIsvCompanyDefinitionService.saveDingtalkIsvCompany(dingtalkIsvCompany);
        return dingtalkIsvCompany;
    }


    /**
     * 1.首位管理员试用-加30天
     * 2.非首位管理员-不调整
     * 3.普通员工不调整
     *
     * @param dingtalkIsvTryOutDTO
     */
    @Override
    public void tryout(DingtalkIsvTryOutDTO dingtalkIsvTryOutDTO) {
        String corpId = dingtalkIsvTryOutDTO.getCorpId();
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        //企业未授权
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String tryoutType = dingtalkIsvTryOutDTO.getTryoutType();
        if (DingtalkIsvConstant.TRYOUT_TYPE_ENTERPRISE.equals(tryoutType)) {
            //1.判断企业是否为首位管理员
            boolean isFirstAdmin = irCompanyService.setCompanyProlongDayAuth(dingtalkIsvCompany.getCompanyId());
            if (isFirstAdmin) {
                String endDateStr = dingtalkIsvTryOutDTO.getEndDate();
                Date endDate = DateUtils.toDate(endDateStr);
                updateCompanyEndDate(corpId, endDate);
            }
        }
        //同步组织机构，并设置管理员
        dingtalkIsvEmployeeService.syncOrgEmployee(corpId);
    }

    public void updateCompanyEndDate(String corpId, Date endDate) {
        log.info("dingtalk isv, 更新合同到期时间");
        String lockKey = MessageFormat.format(RedisKeyConstant.CHANGE_CONTRACT, corpId);
        Long lockTime = RedisDistributionLock.lock(lockKey, redisTemplate);
        if (lockTime > 0) {
            try {
                DingtalkIsvCompany weChatIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
                if (weChatIsvCompany == null) {
                    throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED));
                }
                ContractVo contractVo = iCompanyNewInfoService.queryContract(weChatIsvCompany.getCompanyId());
                Date contractEndDate = DateUtils.toDate(contractVo.getEndDate());
                log.info("dingtalk isv, 更新合同到期时间，oldEndDate={},newEndDate={}", contractEndDate, endDate);
                if (contractEndDate != null && endDate.compareTo(contractEndDate) > 0) {
                    ContractInfoDTO contractInfoDTO = new ContractInfoDTO();
                    contractInfoDTO.setCompanyId(weChatIsvCompany.getCompanyId());
                    contractInfoDTO.setEndDate(endDate);
                    iCompanyNewInfoService.updateCompanyContractInfo(contractInfoDTO);
                }
            } catch (Exception e) {
                log.error("dingtalk isv, 更新合同到期时间失败", e);
                throw e;
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            throw new OpenApiDingtalkException(DingtalkResponseCode.GET_LOCK_FAILED);
        }
    }


}
