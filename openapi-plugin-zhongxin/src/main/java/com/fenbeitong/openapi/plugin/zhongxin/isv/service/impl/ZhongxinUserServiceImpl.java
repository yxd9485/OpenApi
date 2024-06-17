package com.fenbeitong.openapi.plugin.zhongxin.isv.service.impl;

import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.core.util.AesUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportBindEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeBindInfo;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.zhongxin.common.config.CiticBankConfig;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dao.ZhongxinIsvCompanyDao;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvCompany;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvUser;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinBankUtil;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.company.IRCompanyService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dto.UserBindBankDTO;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.ZhongxinEmployeeService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.ZhongxinUserService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.citic.ZhongxinUserAddService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;

/**
 * <p>Title:  ZhongxinUserServiceImpl</p>
 * <p>Description: 用户新增和登陆</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 下午6:34
 **/
@ServiceAspect
@Service
@Slf4j
public class ZhongxinUserServiceImpl implements ZhongxinUserService {

    @Autowired
    private BaseEmployeeRefServiceImpl baseEmployeeRefService;

    @Autowired
    private ZhongxinEmployeeService zhongxinEmployeeService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private ZhongxinUserAddService zhongxinUserAddService;

    @Autowired
    private ZhongxinIsvCompanyDao zhongxinIsvCompanyDao;

    @Autowired
    private CiticBankConfig citicBankConfig;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private IAuthService authService;

    /**
     * 用户新增
     * @param encryptMsg uc传递过来的加密串
     */
    @Override
    public void userAdd(String encryptMsg){
        String zxyhAesKey = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.ZXYH_AES_KEY.getCode());
        String decryptMsg ;
        try {
            decryptMsg = AesUtils.decryptAES(encryptMsg, zxyhAesKey);
        } catch (Exception e) {
            log.error("【中信银行】用户新增UC加密串解密失败", e);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.DECODE_ERROR, "UC数据解密失败");
        }
        UserBindBankDTO userBindBankDTO = JsonUtils.toObj(decryptMsg, UserBindBankDTO.class);

        String overTime = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.ZXYH_BIND_OVER_TIME.getCode());

        //判断链接是否有效
        Long currentTime = System.currentTimeMillis();
        if ((currentTime - userBindBankDTO.getTimeStamp())  > (Long.valueOf(overTime) * 60 * 1000)) {
            log.info("【中信银行】用户绑定公司链接超时,公司ID:{}", userBindBankDTO.getCompanyId());
            throw new OpenApiZhongxinException(ZhongxinResponseCode.AES_ERROR_OVER_TIME);
        }

        //查询分贝通信息
        EmployeeContract employee = zhongxinEmployeeService.getEmployeeByEmployeeId(userBindBankDTO.getCompanyId(),
                userBindBankDTO.getEmployeeId());
        if(null == employee){
            log.info("根据公司id:{}, 员工id:{},未查询到员工信息", userBindBankDTO.getCompanyId(), userBindBankDTO.getEmployeeId());
            throw new OpenApiZhongxinException(ZhongxinResponseCode.ZHONG_XIN_ISV_USER_QUERY_FAILED, "用户信息查询不存在");
        }
        //根据companyId查询对应的三方企业id
        log.info("根据公司id:{}查询中信企业授权信息...", userBindBankDTO.getCompanyId());
        ZhongxinIsvCompany zhongxinIsvCompany = zhongxinIsvCompanyDao.getZhongxinIsvCompanyByCompanyId(userBindBankDTO.getCompanyId());
        if(null == zhongxinIsvCompany){
            log.info("根据公司id:{}未查询到中信企业授权信息", userBindBankDTO.getCompanyId());
            throw new OpenApiZhongxinException(ZhongxinResponseCode.ZHONG_XIN_ISV_COMPANY_UNDEFINED, "企业未授权");
        }

        //查询本地是否已经存在此用户信息
        ZhongxinIsvUser zhongxinIsvUser = zhongxinEmployeeService.getEmployeeInfoByPhoneNum(userBindBankDTO.getPhoneNum(), userBindBankDTO.getCompanyId());
        if(null != zhongxinIsvUser){
            log.info("公司id为:{},手机号为:{}的员工已经存在", userBindBankDTO.getCompanyId(), userBindBankDTO.getPhoneNum());
            throw new OpenApiZhongxinException(ZhongxinResponseCode.USER_AUTH_EXIST, "该用户已经授权");
        }

        //调用中信银行员工新增接口，获取员工id
        String thirdEmployeeId = zhongxinUserAddService.getEmployeeId(zhongxinUserAddService.userAdd(employee, zhongxinIsvCompany.getCorpId()));
        //数据落地
        log.info("中信银行用户信息数据落地...");
        zhongxinEmployeeService.saveEmployeeInfo(employee, thirdEmployeeId);

        //调uc去授权
        SupportBindEmployeeReqDTO supportBindEmployeeReqDTO = SupportBindEmployeeReqDTO.builder().companyId(userBindBankDTO.getCompanyId()).
                operatorId(zhongxinEmployeeService.superAdmin(userBindBankDTO.getCompanyId())).build();
        supportBindEmployeeReqDTO.setBindList(Lists.newArrayList(SupportEmployeeBindInfo.builder().phone(userBindBankDTO.getPhoneNum()).thirdEmployeeId(thirdEmployeeId).build()));
        OpenApiRespDTO openApiRespDTO = zhongxinEmployeeService.bindUserForAPI(supportBindEmployeeReqDTO);

        //通知uc已授权
        if(0 == openApiRespDTO.getCode()){
            IRCompanyService irCompanyService = baseEmployeeRefService.getiRCompanyService();
            log.info("通知UC授权结果返回值为：{}",
                    irCompanyService.bindEmployeeAuthStatus(userBindBankDTO.getCompanyId(), userBindBankDTO.getEmployeeId()));
        }else{
            log.info("调用UC授权结果失败，code={},msg={}", openApiRespDTO.getCode(), openApiRespDTO.getMsg());
            throw new OpenApiPluginSupportException(SupportRespCode.EMPLLYEE_BIND_ERROR, "绑定员工失败");
        }

    }

    /**
     * 获取企业名称
     * @param encryptMsg
     * @return
     */
    @Override
    public String getCompanyName(String encryptMsg){
        String zxyhAesKey = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.ZXYH_AES_KEY.getCode());
        String decryptMsg ;
        try {
            decryptMsg = AesUtils.decryptAES(encryptMsg, zxyhAesKey);
        } catch (Exception e) {
            log.error("【中信银行】获取企业名称UC加密串解密失败", e);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.DECODE_ERROR, "UC数据解密失败");
        }
        UserBindBankDTO userBindBankDTO = JsonUtils.toObj(decryptMsg, UserBindBankDTO.class);
        return userBindBankDTO.getCompanyName();
    }

    /**
     * 用户登陆
     * @param hashEncode
     */
    @Override
    public LoginResVO authLogin(String hashEncode){
        //数据信息解密
        String hashValue ;
        try {
            hashValue = ZhongxinBankUtil.getDecodeStr(hashEncode, citicBankConfig.getDecryptKeyStr());
            log.info("中信银行交易请求明文信息为:{}", hashValue);
            hashValue = JSONObject.fromObject(hashValue).get("HASH").toString();
        }catch (Exception e){
            log.info("数据解密失败！");
            throw new OpenApiZhongxinException(ZhongxinResponseCode.DECODE_ERROR, "数据解密失败");
        }

        log.info("根据银行三方用户id查询本地用户信息...");
        ZhongxinIsvUser zhongxinIsvUser = zhongxinEmployeeService.getEmployeeInfoByHash(hashValue);
        if (null == zhongxinIsvUser) {
            log.info("【中信银行】用户hash={}未绑定分贝通", hashValue);
            throw new OpenApiZhongxinException(ZhongxinResponseCode.ZHONG_XIN_ISV_USER_QUERY_FAILED, "用户不存在");
        }
        String companyId = zhongxinIsvUser.getCompanyId();

        //通过用户手机号获取用户信息
        EmployeeContract employee = employeeExtService.queryEmployeeInfoByPhone(companyId, zhongxinIsvUser.getPhoneNum());
        if (employee == null) {
            throw new OpenApiZhongxinException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_LOGIN_ERROR), "登陆失败");
        }

        return authService.loginAuthInitV5(companyId, employee.getId(), employee.getPhone_num(), IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.CITIC_MARKET.getPlatform(), CompanyLoginChannelEnum.CITIC_MARKET.getEntrance());

    }

}
