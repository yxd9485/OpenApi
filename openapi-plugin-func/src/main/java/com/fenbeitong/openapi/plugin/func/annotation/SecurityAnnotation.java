package com.fenbeitong.openapi.plugin.func.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 安全性注解 包括下面几个校验
 * 0、该接口是否能够提供服务
 * 1、token鉴权
 * 2、白名单
 * 3、接口权限
 * 4、限流
 * 5、幂等
 * 6、计数
 *
 * @author ctl
 * @date 2022/1/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityAnnotation {

}
