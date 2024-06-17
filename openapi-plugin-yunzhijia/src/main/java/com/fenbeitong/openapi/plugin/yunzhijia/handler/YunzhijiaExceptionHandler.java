package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.exception.ExceptionUtil;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaMessageProperties;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResultEntity;
import com.fenbeitong.openapi.plugin.yunzhijia.exception.YunzhijiaException;
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
 * Description: 云之家统一异常处理
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.yunzhijia"})
public class YunzhijiaExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(YunzhijiaException.class)
    public YunzhijiaResultEntity YunzhijiaException(YunzhijiaException YunzhijiaException) {
        log.warn("YunzhijiaException:", YunzhijiaException);
        int code = YunzhijiaException.getCode();
        Object[] args = YunzhijiaException.getArgs();
        String msg = YunzhijiaMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return YunzhijiaResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public YunzhijiaResultEntity openApiPluginSupportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return YunzhijiaResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public YunzhijiaResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        ExceptionUtil.printErrorInfo(exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return YunzhijiaResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public YunzhijiaResultEntity validException(MethodArgumentNotValidException validException) {
        log.error("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return YunzhijiaResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    public YunzhijiaResultEntity openApiBindException(OpenApiBindException bindException) {
        log.error("openApiBindException:", bindException);
        List<ObjectError> allErrors = bindException.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return YunzhijiaResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public YunzhijiaResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return YunzhijiaResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public YunzhijiaResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return YunzhijiaResponseUtils.error(code, msg);
    }
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public YunzhijiaResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return YunzhijiaResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }
    @ResponseBody
    @ExceptionHandler(ResourceAccessException.class)
    public YunzhijiaResultEntity resourceAccessException(ResourceAccessException resourceAccessException) {
        log.error("resourceAccessException:", resourceAccessException);
        String msg = resourceAccessException.getMessage();
        return YunzhijiaResponseUtils.error(RespCode.ERROR, msg);
    }
}
