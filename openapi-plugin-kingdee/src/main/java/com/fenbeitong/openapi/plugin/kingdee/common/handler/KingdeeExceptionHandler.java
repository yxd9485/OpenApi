package com.fenbeitong.openapi.plugin.kingdee.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.prop.FuncMessageProperties;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
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
 * @author ctl
 * @date 2021/8/18
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.kingdee"})
public class KingdeeExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public OpenapiResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return OpenapiResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public OpenapiResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return OpenapiResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    private OpenapiResultEntity openApiBindException(OpenApiBindException e) {
        log.warn("bindException:", e);
        List<ObjectError> allErrors = e.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return OpenapiResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public OpenapiResultEntity validException(MethodArgumentNotValidException validException) {
        log.error("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return OpenapiResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiArgumentException.class)
    public OpenapiResultEntity argumentException(OpenApiArgumentException argumentException) {
        log.warn("argumentException:", argumentException);
        return OpenapiResponseUtils.error(argumentException.getCode(), argumentException.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(OpenApiFuncException.class)
    public OpenapiResultEntity openApiFuncException(OpenApiFuncException openApiFuncException) {
        log.warn("openApiFuncException:", openApiFuncException);
        int code = openApiFuncException.getCode();
        Object[] args = openApiFuncException.getArgs();
        String msg = FuncMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return OpenapiResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public OpenapiResultEntity supportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (ObjectUtils.isEmpty(msg)) {
            msg = FuncMessageProperties.getProperty(String.valueOf(code));
        }
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return OpenapiResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public OpenapiResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return OpenapiResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public OpenapiResultEntity exception(Exception exception) {
        log.warn("exception:", exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return OpenapiResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

}
