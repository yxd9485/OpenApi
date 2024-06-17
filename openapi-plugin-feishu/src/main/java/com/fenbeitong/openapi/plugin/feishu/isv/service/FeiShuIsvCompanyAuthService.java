package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dto.OpenSysConfigReqDTO;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.FundAccountModelType;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuRedisKeyConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.*;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuCompanyAuthService;
import com.fenbeitong.openapi.plugin.feishu.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackAppTicketDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.privilege.service.OpenEmployeePrivService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.PhoneGenUtils;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyAuthorizeDto;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
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

/**
 * 飞书企业授权service
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvCompanyAuthService extends AbstractFeiShuCompanyAuthService {

    @Value("${feishu.isv.appId}")
    private String appId;

    @Value("${feishu.isv.appSecret}")
    private String appSecret;

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;

    @Autowired
    private FeiShuIsvEmployeeService feiShuIsvEmployeeService;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @DubboReference(check = false)
    private ICompanyNewInfoService iCompanyNewInfoService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private OpenEmployeePrivService openEmployeePrivService;


    /**
     * 企业授权
     *
     * @param corpId
     */
    public void companyAuth(String corpId, String userId) {
        log.info("【feishu isv】 companyAuth, 开始处理企业授权");
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                // 1.初始化企业授权信息
                String companyId = initCompany(corpId, userId);
                // 默认打开同步部门主管
                openSysConfigService.saveOpenSysConfigNotExce(
                        OpenSysConfigReqDTO.builder()
                                .type(OpenSysConfigType.OPEN_SET_DEP_MANAGER.getType())
                                .code(companyId)
                                .name("飞书市场版开放部门主管同步")
                                .value("1")
                                .state(1)
                                .build());
                String initRoleTypeFlag = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE_FLAG.getCode());
                if ("true".equals(initRoleTypeFlag)) {
                    // 初始化权限
                    openEmployeePrivService.initRoleType("1", OpenSysConfigCode.CODE_ISV_INIT_ROLE_TYPE.getCode(), companyId);
                }
                // 2.同步组织人员数据
                feiShuIsvEmployeeService.syncFeiShuIsvOrgEmployee(corpId, companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【feishu isv】 companyAuth, 未获取到锁，corpId={}", corpId);
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_LOCK_FAILED);
        }
    }


    /**
     * 取消授权
     *
     * @param corpId
     */
    public void companyCancelAuth(String corpId) {
        log.info("【feishu isv】开始处理企业取消授权");
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        feishuIsvCompany.setState(CompanyAuthState.AUTH_CANCEL.getCode());
        feiShuIsvCompanyDefinitionService.updateFeiShuIsvCompany(feishuIsvCompany);
    }


    /**
     * 初始化企业授权信息
     *
     * @param corpId
     * @param openId
     * @return
     */
    public String initCompany(String corpId, String openId) {
        //1.查看企业是否授权过
        FeishuIsvCompany feiShuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        //2.授权过的企业，更新授权信息
        if (feiShuIsvCompany != null) {
            feiShuIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
            log.info("【feishu isv】initCompany, 开始updateIsvCompany，corpid={}", corpId);
            feiShuIsvCompanyDefinitionService.updateFeiShuIsvCompany(feiShuIsvCompany);
        } else {
            //3.uc创建企业合同
            log.info("【feishu isv】initCompany, 开始uc创建企业, corpid={}", corpId);
            FeiShuUserInfoDTO feiShuUserInfo = feiShuIsvEmployeeService.getUserInfo(FeiShuConstant.ID_TYPE_OPEN_ID, openId, corpId);
            //调用飞书接口查询企业名称
            String companyName = getCompanyName(corpId);
            CompanyCreatVo companyUC = createCompanyUC(feiShuUserInfo, companyName , corpId , "");
            //4.保存企业信息
            log.info("【feishu isv】initCompany, 开始saveIsvCompany，corpid={} , companyName={}", corpId , companyName);
            feiShuIsvCompany = saveIsvCompany(corpId, companyUC.getCompanyId(), companyUC.getCompanyName());
            openCompanySourceTypeService.saveOpenCompanySourceType(companyUC.getCompanyId(), companyUC.getCompanyName(), corpId, OpenType.FEISHU_ISV.getType());
            //log.info("【feishu isv】initCompany, 开始uc创建合同, corpid={}", corpId);
            //addOrUpdateContract(companyUC);
        }
        return feiShuIsvCompany.getCompanyId();
    }

    public Object getCompanyNamelist(String corpId , Integer startIndex , Integer endIndex){
        List<FeishuIsvCompany> list = new ArrayList<>();
        if(StringUtils.isBlank(corpId)){
            //查询所有的三方企业id
            List<FeishuIsvCompany> feiShuIsvAllCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvAllCompany();
            List<FeishuIsvCompany> pageList = new ArrayList<>();
            if(startIndex != null && endIndex != null){
                feiShuIsvAllCompany.sort((o1, o2) -> o2.getId().compareTo(o2.getId()));
                if(endIndex > feiShuIsvAllCompany.size()){
                    endIndex = feiShuIsvAllCompany.size();
                }
                 pageList = feiShuIsvAllCompany.subList(startIndex,endIndex);
            }else{
                pageList =  feiShuIsvAllCompany;
            }
            pageList.forEach( isvCompany->{
                try{
                    FeishuIsvCompany feishuIsvCompany = setFeishuIsvCompany(isvCompany);
                    list.add(feishuIsvCompany);
                }catch (Exception e){
                    log.info("【feishu isv】getCompanyNamelist, 查询企业名称失败, corpId={}", isvCompany.getCorpId());
                }
            });
        }else{
            FeishuIsvCompany isvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
            FeishuIsvCompany feishuIsvCompany = setFeishuIsvCompany(isvCompany);
            list.add(feishuIsvCompany);
        }
        return list;
    }

    private FeishuIsvCompany setFeishuIsvCompany(FeishuIsvCompany isvCompany){
        FeishuIsvCompany feishuIsvCompany = new FeishuIsvCompany();
        String companyName = getCompanyName(isvCompany.getCorpId());
        feishuIsvCompany.setCompanyId( isvCompany.getCompanyId());
        feishuIsvCompany.setCorpId( isvCompany.getCorpId() );
        feishuIsvCompany.setCompanyName( companyName );
        return feishuIsvCompany;
    }


    /**
     * uc创建企业
     *
     * @param feiShuUserInfo
     * @param corpId
     * @param companyNameSuffix
     * @return
     */
    public CompanyCreatVo createCompanyUC(FeiShuUserInfoDTO feiShuUserInfo,String companyName, String corpId, String companyNameSuffix) {
        CompanyCreatVo companyCreatVo = new CompanyCreatVo();
        CompanyAuthorizeDto companyAuth = new CompanyAuthorizeDto();
        companyAuth.setBirthDate(DateUtils.toDate("2020-01-01"));//授权负责人生日  默认2020-01-01
        String uuid = getUUId();
        companyAuth.setThirdUserId(feiShuUserInfo.getOpenId());
        companyAuth.setName(feiShuUserInfo.getName());//授权负责人名称 必填
        if (StringUtils.isBlank(feiShuUserInfo.getEmail())) {
            companyAuth.setEmail(uuid + "@trial.com");//授权负责人邮箱 必填 默认trial@trial.com
        } else {
            companyAuth.setEmail(feiShuUserInfo.getEmail());
        }
        companyAuth.setIdNumber("110101190001011009");//授权负责人证件号 必填 默认虚拟值
        companyAuth.setIdType(EmployeeCert.IdCard.getKey());//授权负责人证件类型 必填
        companyAuth.setMobile(StringUtils.obj2str(virtualPhoneUtils.getVirtualPhone()));//授权负责人手机号 必填 默认虚拟值
        companyAuth.setSex(GenderType.FEMALE.getCode());//授权负责人性别 1 男 2 女 必填 默认女
        companyCreatVo.setThirdCompanyId(corpId);
        companyCreatVo.setCompanyAuth(companyAuth);//授权负责人信息
        companyCreatVo.setCompanySrc(CompanySourceType.LARK.getValue());//企业来源  必填
        companyCreatVo.setCompanyAddress("未设置");//联系地址  未设置
        companyCreatVo.setCompanyCode(uuid);//营业执照号   必填  随机
        companyCreatVo.setCompanyName(companyName + companyNameSuffix);//企业名称  必填
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
        companyCreatVo.setPackageCode(PackageType.LARK_PACKAGE.getKey());//套餐id 必填 企业微信版
        companyCreatVo.setShortCompanyName(companyName + companyNameSuffix);//企业简称
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
                log.info("createCompanyUC ,uc创建企业名称重复，加后缀重试");
                String currentTimeMillis = StringUtils.obj2str(System.currentTimeMillis());
                companyCreatVo.setCompanyName( companyName + "_" + currentTimeMillis);//企业名称  必填
                companyCreatVo.setShortCompanyName(companyName + "_" + currentTimeMillis);//企业简称
                log.info("createCompanyUC companyCreatVo={}, contractVo={}", JsonUtils.toJson(companyCreatVo), JsonUtils.toJson(contractVo));
                companyCreatResult = iCompanyNewInfoService.createCompany(companyCreatVo, contractVo);
                log.info("createCompanyUC res={}", JsonUtils.toJson(companyCreatResult));
                companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
            } else {
                //exceptionRemind.exceptionRemindDingTalk(e);
                throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_CREATE_COMPANY_FAILED, e.getMessage());
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
        String trialDay = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.FEISHU_ISV_TRIAL_DAY.getCode());
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
     * 保存企业授权信息
     */
    private FeishuIsvCompany saveIsvCompany(String corpId, String companyId, String companyName) {
        FeishuIsvCompany feishuIsvCompany = new FeishuIsvCompany();
        feishuIsvCompany.setCompanyId(companyId);
        feishuIsvCompany.setCorpId(corpId);
        feishuIsvCompany.setCompanyName(companyName);
        feishuIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
        feiShuIsvCompanyDefinitionService.saveFeiShuIsvCompany(feishuIsvCompany);
        return feishuIsvCompany;
    }


    /**
     * 缓存app_ticket
     *
     * @param decryptMsg
     */
    public void saveAppTicket(String decryptMsg) {
        FeiShuIsvCallbackAppTicketDTO feiShuIsvCallbackAppTicketDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackAppTicketDTO.class);
        String appTicket = feiShuIsvCallbackAppTicketDTO.getEvent().getAppTicket();
        if (!StringUtils.isBlank(appTicket)) {
            String appTicketKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, FeiShuRedisKeyConstant.FEISHU_ISV_APP_TICKET);
            log.info("【feishu isv】 saveAppTicket,key={},value={}", appTicketKey, appTicket);
            redisTemplate.opsForValue().set(appTicketKey, appTicket);
            // redisTemplate.expire(appTicketKey, 120, TimeUnit.MINUTES);
        }
    }


    /**
     * 获取第三方应用凭证
     *
     * @return
     */
    @Override
    public String getAppAccessToken() {
        // 先尝试从redis查询
        String appAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, FeiShuRedisKeyConstant.FEISHU_ISV_APP_ACCESS_TOKEN);
        String appAccessToken = (String) redisTemplate.opsForValue().get(appAccessTokenKey);
        if (!StringUtils.isBlank(appAccessToken)) {
            return appAccessToken;
        }
        // redis未命中， 重新获取
        String appTicketKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, FeiShuRedisKeyConstant.FEISHU_ISV_APP_TICKET);
        String appTicket = (String) redisTemplate.opsForValue().get(appTicketKey);
        if (StringUtils.isBlank(appTicket)) {
            log.info("【feishu isv】 getAppAccessToken, appTicket获取失败");
            resendAppTicket();
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_APP_TICKET_IS_NULL);
        }
        String url = feishuHost + FeiShuConstant.APP_ACCESS_TOKEN_URL;
        FeiShuAppAccessTokenReqDTO feiShuAppAccessTokenReqDTO = new FeiShuAppAccessTokenReqDTO();
        feiShuAppAccessTokenReqDTO.setAppTicket(appTicket);
        feiShuAppAccessTokenReqDTO.setAppId(appId);
        feiShuAppAccessTokenReqDTO.setAppSecret(appSecret);
        String res = feiShuIsvHttpUtils.postJson(url, JsonUtils.toJson(feiShuAppAccessTokenReqDTO));
        FeiShuAppAccessTokenRespDTO feiShuAppAccessTokenRespDTO = JsonUtils.toObj(res, FeiShuAppAccessTokenRespDTO.class);
        if (feiShuAppAccessTokenRespDTO != null && 99991666 == feiShuAppAccessTokenRespDTO.getCode()) {
            log.info("【feishu isv】 getAppAccessToken, appTicket失效");
            resendAppTicket();
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_APP_TICKET_IS_NULL);
        }
        if (feiShuAppAccessTokenRespDTO == null || 0 != feiShuAppAccessTokenRespDTO.getCode()) {
            log.info("【feishu isv】 appAccessToken获取失败");
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_APP_ACCESS_TOKEN_FAILED);
        }
        appAccessToken = feiShuAppAccessTokenRespDTO.getAppAccessToken();
        // 缓存redis
        log.info("【feishu isv】 saveAppAccessToken,key={},value={}", appAccessTokenKey, appAccessToken);
        redisTemplate.opsForValue().set(appAccessTokenKey, appAccessToken);
        redisTemplate.expire(appAccessTokenKey, feiShuAppAccessTokenRespDTO.getExpire(), TimeUnit.SECONDS);
        return appAccessToken;
    }

    /**
     * 重新发送app_ticket
     */
    public void resendAppTicket() {
        String url = feishuHost + FeiShuConstant.RESEND_APP_TICKET_URL;
        FeiShuAppAccessTokenReqDTO feiShuAppAccessTokenReqDTO = new FeiShuAppAccessTokenReqDTO();
        feiShuAppAccessTokenReqDTO.setAppId(appId);
        feiShuAppAccessTokenReqDTO.setAppSecret(appSecret);
        feiShuIsvHttpUtils.postJson(url, JsonUtils.toJson(feiShuAppAccessTokenReqDTO));
    }


    /**
     * tenant_access_token
     *
     * @param corpId
     * @returnT
     */
    @Override
    public String getTenantAccessTokenByCorpId(String corpId) {
        // 先尝试从redis查询
        String tenantAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_ISV_TENANT_ACCESS_TOKEN, corpId));
        String tenantAccessToken = (String) redisTemplate.opsForValue().get(tenantAccessTokenKey);
        if (!StringUtils.isBlank(tenantAccessToken)) {
            return tenantAccessToken;
        }
        // 未命中缓存， 重新请求
        String getTenantAccessTokenUrl = feishuHost + FeiShuConstant.TENANT_ACCESS_TOKEN_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("tenant_key", corpId);
        String res = feiShuIsvHttpUtils.postJsonWithAppAccessToken(getTenantAccessTokenUrl, param);
        FeiShuTenantAccessTokenRespDTO feiShuTenantAccessTokenRespDTO = JsonUtils.toObj(res, FeiShuTenantAccessTokenRespDTO.class);
        if (feiShuTenantAccessTokenRespDTO == null || StringUtils.isBlank(feiShuTenantAccessTokenRespDTO.getTenantAccessToken())) {
            log.info("【feishu isv】 getTenantAccessTokenByCorpId失败:{}", res);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_GET_TENANT_ACCESS_TOKEN_FAILED);
        }
        tenantAccessToken = feiShuTenantAccessTokenRespDTO.getTenantAccessToken();
        // 缓存redis
        log.info("【feishu isv】 saveTenantAccessToken,key={},value={}", tenantAccessTokenKey, tenantAccessToken);
        redisTemplate.opsForValue().set(tenantAccessTokenKey, tenantAccessToken);
        redisTemplate.expire(tenantAccessTokenKey, feiShuTenantAccessTokenRespDTO.getExpire(), TimeUnit.SECONDS);
        return tenantAccessToken;
    }

    @Override
    public String getTenantAccessTokenByAppIdAndSecret(String appId, String appSecret) {
        return null;
    }


    /**
     * appAccessToken失效，清除redis重新获取
     */
    @Override
    public void clearAppAccessToken() {
        String appAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, FeiShuRedisKeyConstant.FEISHU_ISV_APP_ACCESS_TOKEN);
        redisTemplate.delete(appAccessTokenKey);
    }

    /**
     * tenantAccessToken失效，清除redis重新获取
     *
     * @param corpId
     */
    @Override
    public void clearTenantAccessToken(String corpId) {
        String tenantAccessTokenKey = MessageFormat.format(FeiShuRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(FeiShuRedisKeyConstant.FEISHU_ISV_TENANT_ACCESS_TOKEN, corpId));
        redisTemplate.delete(tenantAccessTokenKey);
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

}
