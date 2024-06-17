package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkMessageProperties;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResultEntity;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception.OpenApiFxkException;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
@Primary
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.fxiaoke"})
public class FxkExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public FxkResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FxkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    private FxkResultEntity openApiBindException(OpenApiBindException e) {
        log.warn("bindException:", e);
        List<ObjectError> allErrors = e.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FxkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FxkResultEntity validException(MethodArgumentNotValidException validException) {
        log.error("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FxkResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiArgumentException.class)
    public FxkResultEntity argumentException(OpenApiArgumentException argumentException) {
        log.warn("argumentException:", argumentException);
        return FxkResponseUtils.error(argumentException.getCode(), argumentException.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(OpenApiFxkException.class)
    public FxkResultEntity openApiFuncException(OpenApiFxkException openApiFxkException) {
        log.warn("openApiFxkException:", openApiFxkException);
        int code = openApiFxkException.getCode();
        Object[] args = openApiFxkException.getArgs();
        String msg = FxkMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return FxkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public FxkResultEntity supportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return FxkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public FxkResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return FxkResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public FxkResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return FxkResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }


}
