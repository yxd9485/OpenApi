package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.FundAccountModelType;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenSyncThirdOrgServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.*;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.constant.WeLinkRedisKeyConstant;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.*;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.util.WeLinkIsvHttpUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * welink企业授权
 * Created by lizhen on 2020/4/14.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvCompanyAuthService {

    @Value("${welink.api-host}")
    private String welinkHost;

    @Value("${welink.isv.appId}")
    private String appId;

    @Value("${welink.isv.appSecret}")
    private String appSecret;

    @Autowired
    private WeLinkIsvCompanyAuthService weLinkIsvCompanyAuthService;

    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private WeLinkIsvHttpUtils weLinkIsvHttpUtils;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @DubboReference(check = false)
    private ICompanyNewInfoService iCompanyNewInfoService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private WeLinkIsvOrganizationService weLinkIsvOrganizationService;

    @Autowired
    private WeLinkIsvEmployeeService weLinkIsvEmployeeService;

    @Autowired
    private OpenSyncThirdOrgServiceImpl openSyncThirdOrgService;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    /**
     * 企业授权体验版
     *
     * @param corpId
     */
    public void companyAuthTrial(String corpId, String userId) {
        log.info("welink isv companyAuthTrial, 开始处理企业授权");
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                // 1.获取企业信息
                WeLinkIsvTenantsInfoRespDTO tenantsInfo = getTenantsInfo(corpId);
                // 2.初始化企业授权信息
                String companyId = initCompany(tenantsInfo, userId);
                // 3.同步组织人员数据
                syncOrgEmployee(corpId, companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【welink isv】 companyAuth, 未获取到锁，corpId={}", corpId);
        }
    }


    /**
     * 取消授权体验版
     *
     * @param corpId
     */
    public void companyCancelAuthTrial(String corpId) {
        log.info("welink isv cancelAuth, 开始处理企业取消授权");
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(corpId);
        if (weLinkIsvCompanyTrial == null) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_COMPANY_UNDEFINED);
        }
        weLinkIsvCompanyTrial.setState(CompanyAuthState.AUTH_CANCEL.getCode());
        weLinkIsvCompanyTrialDefinitionService.updateWeLinkIsvCompanyTrial(weLinkIsvCompanyTrial);
    }

    /**
     * 获取企业详细信息
     */
    public WeLinkIsvTenantsInfoRespDTO getTenantsInfo(String tenantId) {
        String url = welinkHost + WeLinkIsvConstant.GET_TENANTS_URL;
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, null, tenantId);
        log.info("welink isv getTenantsInfo res is {}", res);
        WeLinkIsvTenantsInfoRespDTO weLinkIsvTenantsInfoRespDto = JsonUtils.toObj(res, WeLinkIsvTenantsInfoRespDTO.class);
        if (weLinkIsvTenantsInfoRespDto == null || !"0".equals(weLinkIsvTenantsInfoRespDto.getCode())) {
            log.info("welink isv getTenantsInfo:{}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_TENANTS_INFO_IS_NULL);
        }
        return weLinkIsvTenantsInfoRespDto;
    }

    /**
     * 获取access_token
     *
     * @param corpId
     * @return
     */
    public String getAccessTokenByCorpId(String corpId) {
        // 先尝试从redis查询
        String accessTokenKey = MessageFormat.format(WeLinkRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeLinkRedisKeyConstant.WELINK_ISV_ACCESS_TOKEN, corpId));
        String accessToken = (String) redisTemplate.opsForValue().get(accessTokenKey);
        if (!StringUtils.isBlank(accessToken)) {
            return accessToken;
        }
        // 未命中缓存， 重新请求
        String getCorpTokenUrl = welinkHost + WeLinkIsvConstant.ACCESS_TOKEN_URL;
        WeLinkIsvAccessTokenReqDTO weLinkIsvAccessTokenReqDto = new WeLinkIsvAccessTokenReqDTO();
        weLinkIsvAccessTokenReqDto.setClientId(appId);
        weLinkIsvAccessTokenReqDto.setClientSecret(appSecret);
        // 如果corpId=appId,为本租户access_token,不传tenantId
        if (appId.equals(corpId)) {
            weLinkIsvAccessTokenReqDto.setTenantId(null);
        } else {
            weLinkIsvAccessTokenReqDto.setTenantId(corpId);
        }
        String res = httpUtil.postJson(getCorpTokenUrl, JsonUtils.toJson(weLinkIsvAccessTokenReqDto));
        log.info("welink isv getAccessTokenByCorpId res is {}", res);
        WeLinkIsvAccessTokenRespDTO weLinkIsvAccessTokenRespDto = JsonUtils.toObj(res, WeLinkIsvAccessTokenRespDTO.class);
        if (weLinkIsvAccessTokenRespDto == null || StringUtils.isBlank(weLinkIsvAccessTokenRespDto.getAccessToken())) {
            log.info("welink isv getAccessTokenByCorpId:{}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_ACCESS_TOKEN_IS_NULL);
        }
        accessToken = weLinkIsvAccessTokenRespDto.getAccessToken();
        // 缓存redis
        log.info("welink isv saveAccessToken,key={},value={}", accessTokenKey, accessToken);
        redisTemplate.opsForValue().set(accessTokenKey, accessToken);
        int timeout = weLinkIsvAccessTokenRespDto.getExpiresIn();
        redisTemplate.expire(accessTokenKey, timeout, TimeUnit.SECONDS);
        return accessToken;
    }

    /**
     * 清除accessToken
     */
    public void clearAccessToken(String corpId) {
        String accessTokenKey = MessageFormat.format(WeLinkRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeLinkRedisKeyConstant.WELINK_ISV_ACCESS_TOKEN, corpId));
        redisTemplate.delete(accessTokenKey);
    }

    /**
     * 初始化企业授权信息
     *
     * @param tenantsInfo
     */
    public String initCompany(WeLinkIsvTenantsInfoRespDTO tenantsInfo, String userId) {
        //1.查看企业是否授权过
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(tenantsInfo.getData().getTenantId());
        WeLinkIsvTenantsInfoRespDTO.ResponseData weLinkIsvTenantsInfo = tenantsInfo.getData();
        //2.授权过的企业，更新授权信息
        if (weLinkIsvCompanyTrial != null) {
            weLinkIsvCompanyTrial.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
            log.info("initCompany, 开始updateIsvCompany，corpid={}", weLinkIsvTenantsInfo.getTenantId());
            weLinkIsvCompanyTrialDefinitionService.updateWeLinkIsvCompanyTrial(weLinkIsvCompanyTrial);
        } else {
            //3.uc创建企业合同
            log.info("initCompany, 开始uc创建企业, corpid={}", weLinkIsvTenantsInfo.getTenantId());
            //如果有userId,把管理员信息带进去
            WeLinkIsvUserSimpleRespDTO weLinkIsvUserSimpleRespDTO = null;
            if (!StringUtils.isBlank(userId)) {
                weLinkIsvUserSimpleRespDTO = weLinkIsvEmployeeService.userSimple(userId, weLinkIsvTenantsInfo.getTenantId());
            }
            CompanyCreatVo companyUC = createCompanyUC(weLinkIsvTenantsInfo, weLinkIsvUserSimpleRespDTO, "");
            //4.保存企业信息
            log.info("initCompany, 开始saveIsvCompany，corpid={}", weLinkIsvTenantsInfo.getTenantId());
            weLinkIsvCompanyTrial = saveIsvCompany(weLinkIsvTenantsInfo, companyUC.getCompanyId(), companyUC.getCompanyName());
            openCompanySourceTypeService.saveOpenCompanySourceType(companyUC.getCompanyId(), companyUC.getCompanyName(), weLinkIsvTenantsInfo.getTenantId(), OpenType.WELINK_ISV.getType());
            //log.info("initCompany, 开始uc创建合同, corpid={}", weLinkIsvTenantsInfo.getTenantId());
            //addOrUpdateContract(companyUC);
        }
        return weLinkIsvCompanyTrial.getCompanyId();
    }


    /**
     * uc创建企业
     *
     * @param weLinkIsvTenantsInfo
     */
    public CompanyCreatVo createCompanyUC(WeLinkIsvTenantsInfoRespDTO.ResponseData weLinkIsvTenantsInfo, WeLinkIsvUserSimpleRespDTO weLinkIsvUserSimpleRespDTO, String companyNameSuffix) {
        CompanyCreatVo companyCreatVo = new CompanyCreatVo();
        CompanyAuthorizeDto companyAuth = new CompanyAuthorizeDto();
        companyAuth.setBirthDate(DateUtils.toDate("2020-01-01"));//授权负责人生日  默认2020-01-01
        String uuid = getUUId();
        if (weLinkIsvUserSimpleRespDTO != null) {
            companyAuth.setThirdUserId(weLinkIsvUserSimpleRespDTO.getUserId());
            companyAuth.setName(weLinkIsvUserSimpleRespDTO.getUserNameCn());//授权负责人名称 必填
        } else {
            companyAuth.setThirdUserId(weLinkIsvTenantsInfo.getTenantId());//体验版或拿不到授权负责人id时
            companyAuth.setName(weLinkIsvTenantsInfo.getCompanyContactName());//授权负责人名称 必填
        }
        companyAuth.setEmail(uuid + "@trial.com");//授权负责人邮箱 必填 默认trial@trial.com
        companyAuth.setIdNumber("110101190001011009");//授权负责人证件号 必填 默认虚拟值
        companyAuth.setIdType(EmployeeCert.IdCard.getKey());//授权负责人证件类型 必填
        companyAuth.setMobile(StringUtils.obj2str(virtualPhoneUtils.getVirtualPhone()));//授权负责人手机号 必填 默认虚拟值
        companyAuth.setSex(GenderType.FEMALE.getCode());//授权负责人性别 1 男 2 女 必填 默认女
        companyCreatVo.setThirdCompanyId(weLinkIsvTenantsInfo.getTenantId());
        companyCreatVo.setCompanyAuth(companyAuth);//授权负责人信息
        companyCreatVo.setCompanySrc(CompanySourceType.WELINK_HUAWEIYUN.getValue());//企业来源  必填
        companyCreatVo.setCompanyAddress("未设置");//联系地址  未设置
        companyCreatVo.setCompanyCode(uuid);//营业执照号   必填  随机
        companyCreatVo.setCompanyName(weLinkIsvTenantsInfo.getCompanyNameCn() + companyNameSuffix);//企业名称  必填
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
        companyCreatVo.setPackageCode(PackageType.WELINK_HUAWEIYUN_PACKAGE.getKey());//套餐id 必填 企业微信版
        companyCreatVo.setShortCompanyName(weLinkIsvTenantsInfo.getCompanyNameCn() + companyNameSuffix);//企业简称
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

                companyCreatResult = createCompanyUC(weLinkIsvTenantsInfo, weLinkIsvUserSimpleRespDTO, companyNameSuffix + "(welink)");
                companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
                companyCreatVo.setCompanyName(companyCreatResult.getCompanyName());
            } else {
                //exceptionRemind.exceptionRemindDingTalk(e);
                throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_CREATE_COMPANY_FAILED, e.getMessage());
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
        String trialDay = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WELINK_ISV_TRIAL_DAY.getCode());
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
     */
    private WeLinkIsvCompanyTrial saveIsvCompany(WeLinkIsvTenantsInfoRespDTO.ResponseData weLinkIsvTenantsInfo, String companyId, String companyName) {
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = new WeLinkIsvCompanyTrial();
        weLinkIsvCompanyTrial.setCompanyId(companyId);
        weLinkIsvCompanyTrial.setCorpId(weLinkIsvTenantsInfo.getTenantId());
        weLinkIsvCompanyTrial.setCompanyName(companyName);
        weLinkIsvCompanyTrial.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
        // weLinkIsvCompanyTrial.setThirdAdminId();
        weLinkIsvCompanyTrialDefinitionService.saveWeLinkIsvCompanyTrial(weLinkIsvCompanyTrial);
        return weLinkIsvCompanyTrial;
    }

    /**
     * 全量同步部门人员
     *
     * @param corpId
     */
    private void syncOrgEmployee(String corpId, String companyId) {
        WeLinkIsvCompanyTrial welinkIsvCompanyTrialByCorpId = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(corpId);
        //获取welink全量部门
        List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkIsvDepartmentInfos = weLinkIsvOrganizationService.weLinkDepartmentsList(corpId, "0", welinkIsvCompanyTrialByCorpId.getCompanyName(), 1);
        //获取welink全量人员
        List<WeLinkIsvUsersListRespDTO.WeLinkIsvUserInfo> weLinkIsvUserInfos = new ArrayList<>();
        for (WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo weLinkIsvDepartmentInfo : weLinkIsvDepartmentInfos) {
            String deptCode = weLinkIsvDepartmentInfo.getDeptCode();
            weLinkIsvUserInfos.addAll(weLinkIsvEmployeeService.weLinkUsersList(corpId, deptCode, 1));
        }

        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo weLinkIsvDepartmentInfo : weLinkIsvDepartmentInfos) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitFullName(weLinkIsvDepartmentInfo.getThirdOrgUnitFullName());
            openThirdOrgUnitDTO.setThirdOrgUnitName(weLinkIsvDepartmentInfo.getDeptNameCn());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(weLinkIsvDepartmentInfo.getFatherCode());
            openThirdOrgUnitDTO.setThirdOrgUnitId(weLinkIsvDepartmentInfo.getDeptCode());
            if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
                openThirdOrgUnitDTO.setThirdOrgUnitParentId("");
            }
            if ("0".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
            }
            departmentList.add(openThirdOrgUnitDTO);
        }
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        for (WeLinkIsvUsersListRespDTO.WeLinkIsvUserInfo weLinkIsvUserInfo : weLinkIsvUserInfos) {
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(weLinkIsvUserInfo.getDeptCode());
            openThirdEmployeeDTO.setThirdEmployeeId(weLinkIsvUserInfo.getUserId());
            openThirdEmployeeDTO.setThirdEmployeeName(weLinkIsvUserInfo.getUserNameCn());
            if ("0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
                openThirdEmployeeDTO.setThirdDepartmentId(corpId);
            }
            employeeList.add(openThirdEmployeeDTO);
        }
        //同步
        openSyncThirdOrgService.syncThird(OpenType.WELINK_ISV.getType(), companyId, departmentList, employeeList);
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


}
