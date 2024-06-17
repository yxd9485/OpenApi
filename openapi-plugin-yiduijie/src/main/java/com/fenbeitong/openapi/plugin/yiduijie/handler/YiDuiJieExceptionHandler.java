package com.fenbeitong.openapi.plugin.yiduijie.handler;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieMessageProperties;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResponseUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuiJieExceptionHandler</p>
 * <p>Description: 易对接异常处理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/11 11:07 AM
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.yiduijie"})
public class YiDuiJieExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    public YiDuiJieResultEntity bindException(OpenApiBindException bindException) {
        log.warn("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return YiDuiJieResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join(";", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public YiDuiJieResultEntity validException(MethodArgumentNotValidException validException) {
        log.warn("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return YiDuiJieResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join(";", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiYiDuiJieException.class)
    public YiDuiJieResultEntity openApiYiDuiJieException(OpenApiYiDuiJieException openApiYiDuiJieException) {
        log.warn("OpenApiYiDuiJieException:", openApiYiDuiJieException);
        int code = openApiYiDuiJieException.getCode();
        Object[] args = openApiYiDuiJieException.getArgs();
        String msg = YiDuiJieMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return YiDuiJieResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public YiDuiJieResultEntity openApiPluginSupportException(OpenApiPluginSupportException openApiPluginSupportException) {
        log.warn("openApiPluginSupportException:", openApiPluginSupportException);
        int code = openApiPluginSupportException.getCode();
        Object[] args = openApiPluginSupportException.getArgs();
        String msg = SupportMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        return YiDuiJieResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public YiDuiJieResultEntity exception(Exception exception) {
        log.error("exception:", exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return YiDuiJieResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    public static YiDuiJieResultEntity handlerException(Exception e) {
        YiDuiJieExceptionHandler jieExceptionHandler = SpringUtils.getBean(YiDuiJieExceptionHandler.class);
        if (e instanceof OpenApiBindException) {
            return jieExceptionHandler.bindException((OpenApiBindException) e);
        }
        if (e instanceof MethodArgumentNotValidException) {
            return jieExceptionHandler.validException((MethodArgumentNotValidException) e);
        }
        if (e instanceof OpenApiYiDuiJieException) {
            return jieExceptionHandler.openApiYiDuiJieException((OpenApiYiDuiJieException) e);
        }
        if (e instanceof OpenApiPluginSupportException) {
            return jieExceptionHandler.openApiPluginSupportException((OpenApiPluginSupportException) e);
        }
        return jieExceptionHandler.exception(e);
    }
}
