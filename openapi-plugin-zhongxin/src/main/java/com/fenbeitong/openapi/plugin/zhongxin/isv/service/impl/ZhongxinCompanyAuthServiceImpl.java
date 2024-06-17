package com.fenbeitong.openapi.plugin.zhongxin.isv.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.finhub.common.constant.FundAccountModelType;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportBindEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeBindInfo;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.zhongxin.common.config.CiticBankConfig;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.ZhongxinEmployeeService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic.ZhongxinUserAddService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic.ZhongxinUserQueryService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinBankUtil;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyAuthorizeDto;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractVo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.model.enums.company.BillDayType;
import com.fenbeitong.usercenter.api.model.enums.company.CompanySourceType;
import com.fenbeitong.usercenter.api.model.enums.company.CompanyStatus;
import com.fenbeitong.usercenter.api.model.enums.company.PaymentDayType;
import com.fenbeitong.usercenter.api.model.enums.company.RepaymentDayEmnum;
import com.fenbeitong.usercenter.api.model.enums.employee.EmployeeCert;
import com.fenbeitong.usercenter.api.model.enums.employee.GenderType;
import com.fenbeitong.usercenter.api.model.enums.function.PackageType;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.fenbeitong.openapi.plugin.zhongxin.common.constant.CompanyAuthState;
import com.fenbeitong.openapi.plugin.zhongxin.common.constant.ZhongxinConstant;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dao.ZhongxinIsvCompanyDao;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.ZhongXinCompanyAuthReqDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.ZhongxinUserInfoDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinCodeCheckRespDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.citic.ZhongxinGetMessageRespDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvCompany;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.ZhongxinCompanyAuthService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic.ZhongxinGetMessageService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic.ZhongxinVerifyService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * <p>Title:  ZhongxinCompanyAuthServiceImpl</p>
 * <p>Description: 中信企业授权登陆</p>
 * <p>Company:  中信</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/16 下午4:01
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongxinCompanyAuthServiceImpl implements ZhongxinCompanyAuthService {

    @Autowired
    private ZhongxinIsvCompanyDao zhongxinIsvCompanyDao;

    @Autowired
   // private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private ZhongxinGetMessageService zhongxinGetMessageService;

    @Autowired
    private ZhongxinVerifyService zhongxinVerifyService;

    @Autowired
    private CiticBankConfig citicBankConfig;

    @Autowired
    private ZhongxinUserQueryService zhongxinUserQueryService;

    @Autowired
    private ZhongxinEmployeeService zhongxinEmployeeService;

    @Autowired
    private ZhongxinUserAddService zhongxinUserAddService;

    @DubboReference(check = false)
    private ICompanyNewInfoService iCompanyNewInfoService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private IAuthService authService;

    /**
     * 授权进件
     *
     * @param encrypt
     * @return
     */
    @Override
    public JSONObject companyAuth(String encrypt) {
        JSONObject jsonObject = new JSONObject();
        //获取请求数据明文
        String decryptBusiness;
        try {
            decryptBusiness = ZhongxinBankUtil.getDecodeStr(encrypt, citicBankConfig.getDecryptKeyStr());
            log.info("中信银行交易请求明文信息为:{}", decryptBusiness);
        }catch (Exception e){
            log.info("数据解密失败！");
            throw new OpenApiZhongxinException(ZhongxinResponseCode.DECODE_ERROR, "数据解密失败");
        }

        log.info("【中信银行】 companyAuth, 开始处理企业授权");
        ZhongXinCompanyAuthReqDTO companyAuthReqDTO = JsonUtils.toObj(decryptBusiness, ZhongXinCompanyAuthReqDTO.class);
        String corpId = companyAuthReqDTO.getCorpId();
        String phoneNum = companyAuthReqDTO.getUserId().substring(companyAuthReqDTO.getUserId().length()-4);
        String lockKey = MessageFormat.format(RedisKeyConstant.COMPANY_AUTH_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                //查询本地数据是否存在
                ZhongxinIsvCompany zhongxinIsvCompany = zhongxinIsvCompanyDao.getZhongxinIsvCompanyByCorpId(corpId);
                if(null == zhongxinIsvCompany){
                    //未授权的企业，需要保存企业信息，进行验证
                    log.info("【中信银行】initCompany, 开始saveIsvCompany，corpId={}", corpId);
                    saveIsvCompany(companyAuthReqDTO);
                    //发送短信验证码，重定向分贝通授权验证接口
                    log.info("【中信银行】调用获取短信验证码接口获取授权码...");
                    getMsg(corpId, companyAuthReqDTO.getUserId());
                    jsonObject.put("auth_type", "1");
                    jsonObject.put("corp_id", corpId);
                    jsonObject.put("phone_num", phoneNum);
                    return jsonObject;
                } else if(null != zhongxinIsvCompany && !CompanyAuthState.AUTH_SUCCESS.getCode().equals(zhongxinIsvCompany.getState())
                        && !CompanyAuthState.AUTH_SUCCESS.getCode().equals(companyAuthReqDTO.getUnauthed())){
                    //未授权完成的企业，不第一次授权
                    log.info("【中信银行】initCompany, 开始updateIsvCompany，corpId={}", corpId);
                    updateIsvCompany(zhongxinIsvCompany,companyAuthReqDTO);
                    //发送短信验证码，重定向分贝通授权验证接口
                    log.info("【中信银行】调用获取短信验证码接口获取授权码...");
                    getMsg(corpId, companyAuthReqDTO.getUserId());
                    jsonObject.put("auth_type", "1");
                    jsonObject.put("corp_id", corpId);
                    jsonObject.put("phone_num", phoneNum);
                    return jsonObject;
                }else if(null != zhongxinIsvCompany && !CompanyAuthState.AUTH_SUCCESS.getCode().equals(zhongxinIsvCompany.getState())
                            && CompanyAuthState.AUTH_SUCCESS.getCode().equals(companyAuthReqDTO.getUnauthed())){
                    //本地数据存在，但是没有认证成功，需要自动进行UC开户
                    zhongxinIsvCompany.setUserId(companyAuthReqDTO.getUserId());
                    companyCreatAfter(zhongxinIsvCompany, zhongxinIsvCompany.getUserName());
                }

                //通过用户手机号获取用户信息
                EmployeeContract employee = employeeExtService.queryEmployeeInfoByPhone(zhongxinIsvCompany.getCompanyId(), zhongxinIsvCompany.getUserId());
                if (null == employee) {
                    throw new OpenApiZhongxinException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_LOGIN_ERROR), "登陆失败");
                }
                LoginResVO loginResVO = authService.adminLoginAuthV5(zhongxinIsvCompany.getCompanyId(), employee.getId(), IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.CITIC_MARKET.getPlatform(), CompanyLoginChannelEnum.CITIC_MARKET.getEntrance());
                jsonObject.put("auth_type", "0");
                jsonObject.put("auth_data", loginResVO);
                return jsonObject;
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【中信银行】 companyAuth, 未获取到锁，corpId={}", corpId);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.GET_LOCK_FAILED,"未获取到锁");
        }
    }

    /**
     * 调用银行获取验证码
     * @param corpId
     * @param userId
     * @return
     */
    @Override
    public void getMsg(String corpId, String userId){
        ZhongxinGetMessageRespDTO getMessageRespDTO = zhongxinGetMessageService.getMessage(corpId, userId);
        if(ZhongxinConstant.SUCCESS.equals(getMessageRespDTO.getRETCODE())){
            log.info("调用中信银行获取验证码接口成功");
        }else if(ZhongxinConstant.SEND_SUCCESS.equals(getMessageRespDTO.getRETCODE())){
            log.info("调用中信银行获取验证码接口成功，已发送过验证码，需要去开薪易网页查看");
        }else {
            log.info("调用中信银行获取验证码接口失败");
            throw new OpenApiZhongxinException(ZhongxinResponseCode.GET_MESSAGE_FAILED,getMessageRespDTO.getRETMSG());
        }
    }

    /**
     * 根据页面填写的授权码进行验证
     * @param corpId
     * @param userName
     * @param verifyCode
     * @return
     */
    @Override
    public String verify(String corpId, String userName, String verifyCode){
        //根据corpId进行企业信息查询
        log.info("根据三方企业id进行授权信息查询...");
        ZhongxinIsvCompany zhongxinIsvCompany = zhongxinIsvCompanyDao.getZhongxinIsvCompanyByCorpId(corpId);
        if(null == zhongxinIsvCompany){
            log.info("【中信银行】根据corpId={}未查询到公司授权信息", corpId);
        }
        //先进行名称补充
        zhongxinIsvCompany.setUserName(userName);
        zhongxinIsvCompanyDao.updateById(zhongxinIsvCompany);

        //调用银行授权码授权
        log.info("调用中信银行进行授权码验证...");
        ZhongxinCodeCheckRespDTO codeCheckRespDTO = zhongxinVerifyService.verify(corpId, zhongxinIsvCompany.getUserId(), verifyCode);
        //根据授权码值进行验证
        if(ZhongxinConstant.SUCCESS.equals(codeCheckRespDTO.getRETCODE()) && "1".equals(codeCheckRespDTO.getCHECKRESULT())){
            log.info("【中信银行】corpId={}的企业授权验证成功", corpId);
            //进行企业创建后续处理
            companyCreatAfter(zhongxinIsvCompany, userName);
        }else{
            log.info("【中信银行】corpId={}的企业授权验证失败，失败原因为：{}", corpId, codeCheckRespDTO.getRETMSG());
            throw new OpenApiZhongxinException(ZhongxinResponseCode.VERIFY_MESSAGE_FAILED, codeCheckRespDTO.getRETMSG());
        }
        return null;
    }


    /**
     * 进行UC开户和后续处理
     * @param zhongxinIsvCompany
     * @param userName
     */
    private String companyCreatAfter(ZhongxinIsvCompany zhongxinIsvCompany, String userName){
        //调用UC进行企业进件
        ZhongxinUserInfoDTO zhongxinUserInfoDTO = new ZhongxinUserInfoDTO();
        zhongxinUserInfoDTO.setCorpId(zhongxinIsvCompany.getCorpId());
        zhongxinUserInfoDTO.setCorpName(zhongxinIsvCompany.getCorpName());
        zhongxinUserInfoDTO.setUserName(userName);
        zhongxinUserInfoDTO.setUserId(zhongxinIsvCompany.getUserId());
        //转换三方id
        //log.info("调用中信银行查询三方用户id值...");
        //String hash = zhongxinUserQueryService.queryEmployeeHash(zhongxinIsvCompany.getCorpId(),zhongxinIsvCompany.getUserId());
        zhongxinUserInfoDTO.setHash(zhongxinIsvCompany.getUserId());
        log.info("调用UC进行企业进件...");
        CompanyCreatVo companyUC = createCompanyUC(zhongxinUserInfoDTO, "");

        //创建企业用户信息
        openCompanySourceTypeService.saveOpenCompanySourceType(companyUC.getCompanyId(), companyUC.getCompanyName(), zhongxinIsvCompany.getCorpId(), OpenType.ZHONGXIN_ISV.getType());

        //在中信方创建管理员信息
        log.info("调用中信银行新增用户...");
        EmployeeContract employee = new EmployeeContract();
        employee.setEmployee_id(companyUC.getAdminId());
        employee.setPhone_num(zhongxinIsvCompany.getUserId());
        employee.setName(userName);
        String hash = zhongxinUserAddService.getEmployeeId(zhongxinUserAddService.userAdd(employee, zhongxinIsvCompany.getCorpId()));

        //更新中信此企业的认证状态
        log.info("更新中信企业认证状态等相关信息...");
        zhongxinIsvCompany.setCompanyId(companyUC.getCompanyId());
        zhongxinIsvCompany.setCompanyName(companyUC.getCompanyName());
        zhongxinIsvCompany.setState(CompanyAuthState.AUTH_SUCCESS.getCode());
        zhongxinIsvCompany.setUserId(zhongxinIsvCompany.getUserId());
        zhongxinIsvCompany.setUpdateTime(new Date());
        zhongxinIsvCompanyDao.updateById(zhongxinIsvCompany);

        //经办人落地用户信息
        log.info("企业经办人落地用户信息...");
        zhongxinEmployeeService.saveEmployeeInfo(companyUC.getCompanyId(), companyUC.getAdminId(), zhongxinIsvCompany.getUserId(), userName, hash);

        //更新授权人的三方id
        log.info("更新授权企业管理员的三方id...");
        SupportBindEmployeeReqDTO supportBindEmployeeReqDTO = SupportBindEmployeeReqDTO.builder().companyId(companyUC.getCompanyId())
                .operatorId(zhongxinEmployeeService.superAdmin(companyUC.getCompanyId())).build();
        supportBindEmployeeReqDTO.setBindList(Lists.newArrayList(SupportEmployeeBindInfo.builder().phone(zhongxinIsvCompany.getUserId()).thirdEmployeeId(hash).build()));
        OpenApiRespDTO openApiRespDTO = zhongxinEmployeeService.bindUserForAPI(supportBindEmployeeReqDTO);
        log.info("更新授权企业管理员的三方id结果为：{}", openApiRespDTO.getCode());
        return hash;
    }

    /**
     * uc创建企业
     *
     * @param zhongxinUserInfoDTO
     * @param companyNameSuffix
     * @return
     */
    private CompanyCreatVo createCompanyUC(ZhongxinUserInfoDTO zhongxinUserInfoDTO, String companyNameSuffix) {
        String corpId = zhongxinUserInfoDTO.getCorpId();
        CompanyCreatVo companyCreatVo = new CompanyCreatVo();
        CompanyAuthorizeDto companyAuth = new CompanyAuthorizeDto();
        companyAuth.setBirthDate(DateUtils.toDate("2020-01-01"));//授权负责人生日  默认2020-01-01
        String uuid = getUUId();
        companyAuth.setThirdUserId(zhongxinUserInfoDTO.getHash());
        companyAuth.setName(zhongxinUserInfoDTO.getUserName());//授权负责人名称 必填
        companyAuth.setEmail(uuid + "@trial.com");//授权负责人邮箱 必填 默认trial@trial.com
        companyAuth.setIdNumber("110101190001011009");//授权负责人证件号 必填 默认虚拟值
        companyAuth.setIdType(EmployeeCert.IdCard.getKey());//授权负责人证件类型 必填
        companyAuth.setMobile(zhongxinUserInfoDTO.getUserId());//授权负责人手机号 必填
        companyAuth.setSex(GenderType.FEMALE.getCode());//授权负责人性别 1 男 2 女 必填 默认女
        companyCreatVo.setThirdCompanyId(corpId);
        companyCreatVo.setCompanyAuth(companyAuth);//授权负责人信息
        companyCreatVo.setCompanySrc(CompanySourceType.CITICIB.getValue());//企业来源  必填
        companyCreatVo.setCompanyAddress("未设置");//联系地址  未设置
        companyCreatVo.setCompanyCode(uuid);//营业执照号   必填  随机
        companyCreatVo.setCompanyName(zhongxinUserInfoDTO.getCorpName() + companyNameSuffix);//企业名称  必填
        companyCreatVo.setCooperatingModel(FundAccountModelType.RECHARGE.getKey());//合作模式 1授信 2充值 必填, 默认充值模式。
        companyCreatVo.setBusinessAccountModel(FundAccountModelType.RECHARGE.getKey());
        companyCreatVo.setPersonalAccountModel(FundAccountModelType.RECHARGE.getKey());
        companyCreatVo.setCooperatingState(CompanyStatus.TRIAL.getKey());//合作状态 ，默认试用
        companyCreatVo.setCreateTime(DateUtils.toSimpleStr(DateUtils.now(), true));//准入日期 必填
        companyCreatVo.setEnterpriseLevel(3);//企业等级， 默认B
        companyCreatVo.setInitCredit(BigDecimal.ZERO);//授信额度 必填 默认0
        companyCreatVo.setOperateorId(1L);//操作人id 必填
        companyCreatVo.setOperateorMobile(zhongxinUserInfoDTO.getUserId());//操作人手机 必填
        companyCreatVo.setOperateorName(companyAuth.getName());//操作人名称 必填
        companyCreatVo.setPackageCode(PackageType.CITICIB_STANDARD_PACKAGE.getKey());//套餐id 必填 企业微信版
        companyCreatVo.setShortCompanyName(zhongxinUserInfoDTO.getCorpName() + companyNameSuffix);//企业简称
        CompanyCreatVo companyCreatResult = null;
        ContractVo contractVo = addOrUpdateContract(companyCreatVo);
        try {
            log.info("createCompanyUC companyCreatVo={}, contractVo={}", JsonUtils.toJson(companyCreatVo), JsonUtils.toJson(contractVo));
            companyCreatResult = iCompanyNewInfoService.createCompany(companyCreatVo, contractVo);
            log.info("createCompanyUC res={}", JsonUtils.toJson(companyCreatResult));
            companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
            companyCreatVo.setAdminId(companyCreatResult.getAdminId());
        } catch (FinhubException e) {
            log.error("createCompanyUC ,uc创建企业失败", e);
            if (e.getCode() == 20089) {
                log.info("createCompanyUC ,uc创建企业名称重复，加后缀重试");
                companyCreatResult = createCompanyUC(zhongxinUserInfoDTO, companyNameSuffix + "(citic)");
                companyCreatVo.setCompanyId(companyCreatResult.getCompanyId());
                companyCreatVo.setCompanyName(companyCreatResult.getCompanyName());
                companyCreatVo.setAdminId(companyCreatResult.getAdminId());
            } else {
                throw new OpenApiZhongxinException(ZhongxinResponseCode.ZHONG_XIN_ISV_CREATE_COMPANY_FAILED, e.getMessage());
            }
        }
        log.info("createCompanyUC, 创建企业成功");
        return companyCreatVo;
    }


    /**
     * 保存企业授权信息
     */
    private ZhongxinIsvCompany saveIsvCompany(ZhongXinCompanyAuthReqDTO companyAuthReqDTO) {
        ZhongxinIsvCompany zhongxinIsvCompany = new ZhongxinIsvCompany();
        zhongxinIsvCompany.setCorpId(companyAuthReqDTO.getCorpId());
        zhongxinIsvCompany.setCorpName(companyAuthReqDTO.getCorpName());
        zhongxinIsvCompany.setUserId(companyAuthReqDTO.getUserId());
        zhongxinIsvCompany.setAppId(companyAuthReqDTO.getAppId());
        zhongxinIsvCompany.setCorpCode(companyAuthReqDTO.getCorpCode());
        zhongxinIsvCompany.setScCode(companyAuthReqDTO.getScCode());
        zhongxinIsvCompany.setCreateTime(new Date());
        zhongxinIsvCompany.setUpdateTime(new Date());
        zhongxinIsvCompany.setState(CompanyAuthState.AUTH_INIT.getCode());
        zhongxinIsvCompanyDao.saveSelective(zhongxinIsvCompany);
        return zhongxinIsvCompany;
    }

    /**
     * 更新企业授权信息
     */
    private ZhongxinIsvCompany updateIsvCompany(ZhongxinIsvCompany zhongxinIsvCompany, ZhongXinCompanyAuthReqDTO companyAuthReqDTO) {
        zhongxinIsvCompany.setCorpName(companyAuthReqDTO.getCorpName());
        zhongxinIsvCompany.setUserId(companyAuthReqDTO.getUserId());
        zhongxinIsvCompany.setAppId(companyAuthReqDTO.getAppId());
        zhongxinIsvCompany.setCorpCode(companyAuthReqDTO.getCorpCode());
        zhongxinIsvCompany.setScCode(companyAuthReqDTO.getScCode());
        zhongxinIsvCompany.setUpdateTime(new Date());
        zhongxinIsvCompanyDao.updateById(zhongxinIsvCompany);
        return zhongxinIsvCompany;
    }

    /**
     * 新增企业合同
     *
     * @param companyCreatVo
     * @return
     */
    private ContractVo addOrUpdateContract(CompanyCreatVo companyCreatVo) {
        ContractVo contractVo = new ContractVo();
        contractVo.setCompanyId(companyCreatVo.getCompanyId());
        contractVo.setCompanyName(companyCreatVo.getCompanyName());
        contractVo.setCode(companyCreatVo.getCompanyAuth().getMobile());//合同code 虚拟值
        contractVo.setBeginDate(companyCreatVo.getCreateTime());//生效日期
        //试用期天数，未配置默认为30天
        String trialDay = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.ZXYH_ISV_TRIAL_DAY.getCode());
        if (StringUtils.isBlank(trialDay)) {
            trialDay = "30";
        }
        contractVo.setEndDate(DateUtils.afterDay(Integer.valueOf(trialDay)));
        contractVo.setRate(BigDecimal.ZERO);//费率默认0
        contractVo.setBillDay(BillDayType.ONE.getValue());//账单日 默认1
        contractVo.setRepaymentDay(RepaymentDayEmnum.ONE_THIRD_KIND.getRepaymentDay());//还款日
        contractVo.setPlatformFee(BigDecimal.ZERO);//平台服务费 默认0
        contractVo.setCooperatingModel(companyCreatVo.getCooperatingModel());//合作模式 充值
        contractVo.setPaymentDays(PaymentDayType.FIRST_KIND.getKey());//账期 默认30+15
        contractVo.setRenew(false);
        return contractVo;
    }


    /**
     * 获取18位随机数
     *
     * @return
     */
    private static String getUUId() {
        int first = new Random(10).nextInt(8) + 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // d 代表参数为正数型
        return first + String.format("%017d", hashCodeV);
    }

}
