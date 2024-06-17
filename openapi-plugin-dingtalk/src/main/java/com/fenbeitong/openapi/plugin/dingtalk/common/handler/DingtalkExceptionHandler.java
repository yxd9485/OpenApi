package com.fenbeitong.openapi.plugin.dingtalk.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkMessageProperties;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResultEntity;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.exception.ExceptionUtil;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: DingtalkExceptionHandler</p>
 * <p>Description: 钉钉异常处理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/20 4:47 PM
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.dingtalk"})
public class DingtalkExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(OpenApiDingtalkException.class)
    public DingtalkResultEntity dingtalkException(OpenApiDingtalkException exception) {
        log.warn("OpenApiDingtalkException ", exception);
        int code = exception.getCode();
        Object[] args = exception.getArgs();
        String msg = DingtalkMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null)
            msg = String.format(msg, args);
        return DingtalkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public DingtalkResultEntity exception(Throwable exception) {
        log.error("exception:", exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return DingtalkResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }


    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public DingtalkResultEntity openApiPluginSupportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return DingtalkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public DingtalkResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        ExceptionUtil.printErrorInfo(exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return DingtalkResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DingtalkResultEntity validException(MethodArgumentNotValidException validException) {
        log.error("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DingtalkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    public DingtalkResultEntity openApiBindException(OpenApiBindException openApiBindException) {
        log.warn("openApiBindException:", openApiBindException);
        List<ObjectError> allErrors = openApiBindException.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DingtalkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public DingtalkResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DingtalkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public DingtalkResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return DingtalkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public DingtalkResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return DingtalkResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }
}
