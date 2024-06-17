package com.fenbeitong.openapi.plugin.func.sso.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.sso.dao.OpenYufuSsoConfigDao;
import com.fenbeitong.openapi.plugin.func.sso.entity.OpenYufuSsoConfig;
import com.fenbeitong.openapi.plugin.func.sso.service.IYuFuSsoService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Maps;
import com.yufu.idaas.sdk.constants.SDKRole;
import com.yufu.idaas.sdk.init.IYufuAuth;
import com.yufu.idaas.sdk.init.YufuAuth;
import com.yufu.idaas.sdk.token.JWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: YuFuSsoServiceImpl</p>
 * <p>Description: 分贝通玉符单点登录</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 7:44 PM
 */
@Slf4j
@ServiceAspect
@Service
public class YuFuSsoServiceImpl implements IYuFuSsoService {

    @Value("${host.fbtweb}")
    private String fbtWebHost;

    @Value("${host.openplus}")
    private String openPlusHost;

    @Autowired
    private OpenYufuSsoConfigDao yufuSsoConfigDao;

    @DubboReference(check = false)
    private IAuthService authService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object loginWeb(String companyId, String token) {
        OpenYufuSsoConfig yufuSsoConfig = yufuSsoConfigDao.getByCompanyIdPlatformType(companyId, 1);
        if (yufuSsoConfig == null) {
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_LOGIN_ERROR));
        }
        try {
            LoginResVO loginResVO = getLoginResByYufu(companyId, token, yufuSsoConfig);
            String id = DateUtils.toStr(new Date(), "yyyyMMddHHmmssSSS") + RandomUtils.randomStr(6);
            final String cacheKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, "yufu_oos_login_info" + id);
            redisTemplate.opsForValue().set(cacheKey, JsonUtils.toJson(loginResVO), 10, TimeUnit.MINUTES);
            MultiValueMap<String, String> headers = new HttpHeaders();
            String location = String.format("%s/jump/login?url=%s/openapi/func/yufu/sso/getWebLoginInfo/%s", fbtWebHost, openPlusHost, id);
            headers.add("Location", location);
            return new ResponseEntity(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.warn("玉符免登失败", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_LOGIN_ERROR));
        }
    }

    @Override
    public Object loginWebapp(String companyId, String token) {
        OpenYufuSsoConfig yufuSsoConfig = yufuSsoConfigDao.getByCompanyIdPlatformType(companyId, 2);
        if (yufuSsoConfig == null) {
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_APP_LOGIN_ERROR));
        }
        try {
            LoginResVO loginResVO = getLoginResByYufu(companyId, token, yufuSsoConfig);
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("loginResVo", loginResVO);
            return resultMap;
        } catch (Exception e) {
            log.warn("玉符免登失败", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_APP_LOGIN_ERROR));
        }
    }

    private LoginResVO getLoginResByYufu(String companyId, String token, OpenYufuSsoConfig yufuSsoConfig) throws Exception {
        IYufuAuth serviceProvider = YufuAuth.builder()
                .sdkRole(SDKRole.SP)
                .publicKeyString(yufuSsoConfig.getPublicKey())
                .tenant(yufuSsoConfig.getTenant())
                .issuer(yufuSsoConfig.getIssuer())
                .audience(yufuSsoConfig.getAudience())
                .build();
        // 使用验证玉符SDK实例进行验证, 如果成功会返回包含用户信息的对象，失败则会产生授权错误的异常
        JWT claims = serviceProvider.verify(token);
        log.info("玉符登录信息,claims={}", JsonUtils.toJson(claims.getClaims()));
        // 用户名称
        String username = null;
        //用户名类型 1:手机号;2:邮箱
        Integer userNameType = yufuSsoConfig.getUserNameType();
        if (userNameType == 1) {
            username = (String) claims.getClaims().get("user_phone");
        } else {
            username = (String) claims.getClaims().get("user_email");
        }
        //用户信息
        EmployeeContract employee = getEmployee(companyId, username, userNameType);
        if (employee == null) {
            throw new OpenApiFuncException(NumericUtils.obj2int(yufuSsoConfig.getPlatformType() == 1 ? FuncResponseCode.FBT_WEB_LOGIN_ERROR : FuncResponseCode.FBT_WEB_LOGIN_ERROR));
        }
        if (yufuSsoConfig.getPlatformType() == 1) {
            return authService.adminLoginAuthV5(companyId, employee.getId(), IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.FXIAOKE_H5.getPlatform(), CompanyLoginChannelEnum.FXIAOKE_H5.getEntrance());
        } else if (yufuSsoConfig.getPlatformType() == 2) {
            return authService.loginAuthInitV5(companyId, employee.getId(), employee.getPhone_num(), IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.FXIAOKE_H5.getPlatform(), CompanyLoginChannelEnum.FXIAOKE_H5.getEntrance());
        }
        return null;
    }

    private EmployeeContract getEmployee(String companyId, String username, Integer userNameType) {
        EmployeeContract employee = null;
        //1:手机号
        if (userNameType == 1) {
            employee = employeeExtService.queryEmployeeInfoByPhone(companyId, username);
        }
        //2:邮箱
        else if (userNameType == 2) {
            List<EmployeeContract> employeeContracts = employeeExtService.queryByEmailAndCompanyId(companyId, username);
            employee = !ObjectUtils.isEmpty(employeeContracts) ? employeeContracts.get(0) : null;
        }
        return employee;
    }

    @Override
    public Object getWebLoginInfo(String id) {
        final String cacheKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, "yufu_oos_login_info" + id);
        String loginInfo = (String) redisTemplate.opsForValue().get(cacheKey);
        LoginResVO loginResVO = JsonUtils.toObj(loginInfo, LoginResVO.class);
        if (loginResVO == null) {
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_LOGIN_ERROR));
        }
        return loginResVO;
    }
}
