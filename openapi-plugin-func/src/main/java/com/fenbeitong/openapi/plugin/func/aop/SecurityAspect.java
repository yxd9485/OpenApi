package com.fenbeitong.openapi.plugin.func.aop;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.func.common.OpenApiConstant;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全切面 最高优先级
 *
 * @author ctl
 * @date 2022/1/19
 */
@Component
@Aspect
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityAspect {


    /**
     * 定义切点的方法，可获取注解标注处配置参数，也可以获取目标方法注入的参数
     */
    @Pointcut("@annotation(com.fenbeitong.openapi.plugin.func.annotation.SecurityAnnotation)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object toAfterAdvance(ProceedingJoinPoint pjp) throws Throwable {
        //获取request和response
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new FinhubException(500, "未获取到request对象");
        }

        HttpServletRequest request = attributes.getRequest();
        String appId = request.getHeader(OpenApiConstant.APPID);
        if (StringUtils.isBlank(appId)) {
            throw new OpenApiArgumentException("未获取到企业id");
        }
        request.setAttribute(OpenApiConstant.COMPANY_ID, appId);
        return pjp.proceed();
    }

}
