package com.fenbeitong.openapi.plugin.func.aop;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.util.ApiJwtTokenTool;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


/**
 * 权限拦截器
 *
 * @author xiaowei
 * @date 2020/05/25
 */

@Component
@Aspect
@Order(1)
@Slf4j
public class FuncAuthInterceptor {

    private static final String PARAM_ERROR = "参数错误";
    @Value("${openapi.timestamp.gap}")
    private long gap;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private ApiJwtTokenTool jwtTokenTool;


    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    //定义切点的方法，可获取注解标注处配置参数，也可以获取目标方法注入的参数
    @Pointcut(value = "@annotation(annotation)")
    //@Pointcut(value = "@annotation(authVervificationAnnotation) && args(test, param)", argNames = "param, test")
    public void pointCut(FuncAuthAnnotation annotation) {
    }

    @Around(value = "pointCut(annotation)")
    public Object toAfterAdvance(ProceedingJoinPoint pjp, FuncAuthAnnotation annotation) throws Throwable {
        //获取request和response
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        String requestURI = request.getRequestURI();
        //MethodSignature signature = (MethodSignature) pjp.getSignature();
        //AuthVerificationAnnotation ann = signature.getMethod().getAnnotation(AuthVerificationAnnotation.class);
        if (annotation != null && Boolean.valueOf(annotation.value())) {
            //1、必传参数的校验 2、accessToken 3、sign的校验
            verificationData(request);
            Object o = pjp.proceed();
            return o;
        } else {
            //此处不进行校验
            //执行controller方法，o为该方法的返回值，也可以对返回值进行处理
            return pjp.proceed();
        }
    }


    private void verificationData(HttpServletRequest request) {
        //必要参数的校验
        String accessToken = request.getParameter("access_token");
        String sign = request.getParameter("sign");
        String timestamp = request.getParameter("timestamp");
        if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(sign) || StringUtils.isEmpty(timestamp)) {
            throw new OpenApiArgumentException(PARAM_ERROR);
        }
        //accessToken的校验
        DecodedJWT jwt = jwtTokenTool.verifyToken(accessToken);
        String appId = jwt.getClaim("appId").asString();
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(appId);
        if (authDefinition == null) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.TOKEN_INFO_IS_ERROR));
        }
        //sign 的校验
        String data = request.getParameter("data");
        String expectSign = SignTool.genSign(Long.valueOf(timestamp), data, authDefinition.getSignKey());
        if (!expectSign.equals(sign)) {
            log.info("expectSign is " + expectSign + ",but request sign is " + sign);
            //线上环境
            if (profile.toLowerCase().contains("pro")) {
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.SIGN_ERROR));
            } else {
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.SIGN_ERROR_TIP), expectSign, sign);
            }
        }
        OffsetDateTime nowTime = Instant.now().atOffset(ZoneOffset.ofHours(8));
        OffsetDateTime requestTime = Instant.ofEpochMilli(Long.valueOf(timestamp)).atOffset(ZoneOffset.ofHours(8));
        //线上才处理 请求的时间戳加上10分钟后还在现在的时间之前,则表明至少十分钟以前的请求了
        if (profile.toLowerCase().contains("pro") && requestTime.plusMinutes(gap).isBefore(nowTime)) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.TIMESTAMP_NO_EFFECT));
        }
        request.setAttribute("companyId", appId);
    }

}
