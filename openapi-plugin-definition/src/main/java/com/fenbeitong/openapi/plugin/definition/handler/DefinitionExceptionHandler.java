package com.fenbeitong.openapi.plugin.definition.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.exception.OpenApiDefinitionException;
import com.fenbeitong.openapi.plugin.definition.prop.DefinitionMessageProperties;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResultEntity;
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

/**
 * 系统对接配置异常处理
 * Created by log.chang on 2019/12/13.
 */
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.definition"})
@Slf4j
public class DefinitionExceptionHandler {

    @ResponseBody
    @ExceptionHandler(OpenApiDefinitionException.class)
    public DefinitionResultDTO funcArgumentException(OpenApiDefinitionException exception) {
        log.info("OpenApiDefinitionException ", exception);
        int code = exception.getCode();
        Object[] args = exception.getArgs();
        String msg = DefinitionMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null)
            msg = String.format(msg, args);
        return DefinitionResultDTO.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public DefinitionResultDTO funcArgumentException(OpenApiPluginSupportException exception) {
        log.info("Exception ", exception);
        int code = exception.getCode();
        Object[] args = exception.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null)
            msg = String.format(msg, args);
        return DefinitionResultDTO.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public DefinitionResultDTO exception(Exception exception) {
        log.info("Exception ", exception);
        return DefinitionResultDTO.error(RespCode.ERROR, "系统繁忙，请稍后重试...");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DefinitionResultDTO validException(MethodArgumentNotValidException validException) {
        log.info("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DefinitionResultDTO.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public DefinitionResultDTO finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return DefinitionResultDTO.error(code, msg);
    }


    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public DefinitionResultDTO missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return DefinitionResultDTO.error(RespCode.ARGUMENT_ERROR, msg);
    }
}
