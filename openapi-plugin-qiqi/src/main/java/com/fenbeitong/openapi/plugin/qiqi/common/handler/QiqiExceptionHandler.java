package com.fenbeitong.openapi.plugin.qiqi.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiMessageProperties;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.qiqi"})
@Slf4j
public class QiqiExceptionHandler {

    @ResponseBody
    @ExceptionHandler(OpenApiQiqiException.class)
    public QiqiResultEntity funcArgumentException(OpenApiQiqiException exception) {
        log.info("OpenApiDefinitionException ", exception);
        int code = exception.getCode();
        Object[] args = exception.getArgs();
        String msg = QiqiMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null)
            msg = String.format(msg, args);
        return QiqiResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public QiqiResultEntity funcArgumentException(OpenApiPluginSupportException exception) {
        log.info("Exception ", exception);
        int code = exception.getCode();
        Object[] args = exception.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null)
            msg = String.format(msg, args);
        return QiqiResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public QiqiResultEntity exception(Exception exception) {
        log.info("Exception ", exception);
        return QiqiResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后重试...");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public QiqiResultEntity validException(MethodArgumentNotValidException validException) {
        log.info("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return QiqiResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public QiqiResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return QiqiResponseUtils.error(code, msg);
    }


    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public QiqiResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return QiqiResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }
}
