package com.fenbeitong.openapi.plugin.wechat.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.exception.ExceptionUtil;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatMessageProperties;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResultEntity;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能集成异常处理
 * <p>Title: FuncControllerAdvice</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/11 11:07 AM
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.wechat"})
public class WechatExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(OpenApiWechatException.class)
    public WechatResultEntity openApiWechatException(OpenApiWechatException openApiWechatException) {
        log.warn("openApiWechatException:", openApiWechatException);
        int code = openApiWechatException.getCode();
        Object[] args = openApiWechatException.getArgs();
        String msg = WechatMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return WechatResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public WechatResultEntity openApiPluginSupportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return WechatResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public WechatResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        ExceptionUtil.printErrorInfo(exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return WechatResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WechatResultEntity validException(MethodArgumentNotValidException validException) {
        log.error("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return WechatResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    public WechatResultEntity openApiBindException(OpenApiBindException bindException) {
        log.warn("openApiBindException:", bindException);
        List<ObjectError> allErrors = bindException.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return WechatResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public WechatResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return WechatResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public WechatResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return WechatResponseUtils.error(code, msg);
    }
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public WechatResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return WechatResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }
    @ResponseBody
    @ExceptionHandler(ResourceAccessException.class)
    public WechatResultEntity resourceAccessException(ResourceAccessException resourceAccessException) {
        log.error("resourceAccessException:", resourceAccessException);
        String msg = resourceAccessException.getMessage();
        return WechatResponseUtils.error(RespCode.ERROR, msg);
    }

    public static WechatResultEntity handlerException(Exception e) {
        WechatExceptionHandler wechatExceptionHandler = SpringUtils.getBean(WechatExceptionHandler.class);
        if (e instanceof BindException) {
            return wechatExceptionHandler.bindException((BindException) e);
        }
        if (e instanceof OpenApiBindException) {
            return wechatExceptionHandler.openApiBindException((OpenApiBindException) e);
        }
        if (e instanceof MethodArgumentNotValidException) {
            return wechatExceptionHandler.validException((MethodArgumentNotValidException) e);
        }
        if (e instanceof OpenApiWechatException) {
            return wechatExceptionHandler.openApiWechatException((OpenApiWechatException) e);
        }
        if (e instanceof OpenApiPluginSupportException) {
            return wechatExceptionHandler.openApiPluginSupportException((OpenApiPluginSupportException) e);
        }
        if (e instanceof FinhubException) {
            return wechatExceptionHandler.finhubException((FinhubException) e);
        }
        if (e instanceof MissingServletRequestParameterException) {
            return wechatExceptionHandler.missingServletRequestParameterException((MissingServletRequestParameterException) e);
        }
        return wechatExceptionHandler.exception(e);
    }
}
