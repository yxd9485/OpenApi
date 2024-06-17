package com.fenbeitong.openapi.plugin.feishu.common.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuMessageProperties;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResultEntity;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
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
 * 功能集成异常处理
 * <p>Title: FuncControllerAdvice</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/11 11:07 AM
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.feishu"})
public class FeiShuExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(OpenApiFeiShuException.class)
    public FeiShuResultEntity openApiFeiShuException(OpenApiFeiShuException openApiFeiShuException) {
        log.warn("openApiFeiShuException:", openApiFeiShuException);
        int code = openApiFeiShuException.getCode();
        Object[] args = openApiFeiShuException.getArgs();
        String msg = FeiShuMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return FeiShuResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public FeiShuResultEntity openApiPluginSupportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return FeiShuResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public FeiShuResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        ExceptionUtil.printErrorInfo(exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return FeiShuResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FeiShuResultEntity validException(MethodArgumentNotValidException validException) {
        log.error("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FeiShuResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    public FeiShuResultEntity openApiBindException(OpenApiBindException openApiBindException) {
        log.warn("openApiBindException:", openApiBindException);
        List<ObjectError> allErrors = openApiBindException.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FeiShuResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public FeiShuResultEntity bindException(BindException bindException) {
        log.error("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FeiShuResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public FeiShuResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return FeiShuResponseUtils.error(code, msg);
    }
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public FeiShuResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return FeiShuResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }
}
