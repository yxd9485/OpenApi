package com.fenbeitong.openapi.plugin.welink.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkMessageProperties;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResultEntity;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
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
 * 功能集成异常处理
 * <p>Title: FuncControllerAdvice</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/11 11:07 AM
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.welink"})
public class WeLinkExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(OpenApiWeLinkException.class)
    public WeLinkResultEntity openApiWeLinkException(OpenApiWeLinkException openApiWeLinkException) {
        log.error("openApiWeLinkException:", openApiWeLinkException);
        int code = openApiWeLinkException.getCode();
        Object[] args = openApiWeLinkException.getArgs();
        String msg = WeLinkMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return WeLinkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public WeLinkResultEntity openApiPluginSupportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.error("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return WeLinkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public WeLinkResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return WeLinkResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WeLinkResultEntity validException(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("methodArgumentNotValidException:", methodArgumentNotValidException);
        List<ObjectError> allErrors = methodArgumentNotValidException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return WeLinkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    public WeLinkResultEntity bindException(OpenApiBindException openApiBindException) {
        log.error("openApiBindException:", openApiBindException);
        List<ObjectError> allErrors = openApiBindException.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return WeLinkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public WeLinkResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return WeLinkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public WeLinkResultEntity finhubException(FinhubException finhubException) {
        log.error("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return WeLinkResponseUtils.error(code, msg);
    }
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public WeLinkResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return WeLinkResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }

}
