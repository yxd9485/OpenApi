package com.fenbeitong.openapi.plugin.func.handler;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.prop.FuncMessageProperties;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.properties.SupportMessageProperties;
import com.fenbeitong.openapi.plugin.support.exception.ExceptionUtil;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.core.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.func"})
public class FuncExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public FuncResultEntity bindException(BindException bindException) {
        log.warn("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FuncResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public FuncResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.warn("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return FuncResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    private FuncResultEntity openApiBindException(OpenApiBindException e) {
        log.warn("bindException:", e);
        List<ObjectError> allErrors = e.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FuncResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FuncResultEntity validException(MethodArgumentNotValidException validException) {
        log.warn("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return FuncResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public FuncResultEntity validException(ConstraintViolationException constraintViolationException) {
        log.warn("constraintViolationException:", constraintViolationException);
        Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        List<String> errorList = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return FuncResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiArgumentException.class)
    public FuncResultEntity argumentException(OpenApiArgumentException argumentException) {
        log.warn("argumentException:", argumentException);
        return FuncResponseUtils.error(argumentException.getCode(), argumentException.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(OpenApiFuncException.class)
    public FuncResultEntity openApiFuncException(OpenApiFuncException openApiFuncException) {
        log.warn("openApiFuncException:", openApiFuncException);
        int code = openApiFuncException.getCode();
        Object[] args = openApiFuncException.getArgs();
        String msg = FuncMessageProperties.getProperty(String.valueOf(code));
        if (!StringUtils.isTrimBlank(msg) && args != null) {
            msg = String.format(msg, args);
        }
        if (StringUtils.isTrimBlank(msg) && Objects.nonNull(args) && args.length != 0) {
            msg = Objects.isNull(args[0]) ? null : args[0].toString();
        }
        return FuncResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiPluginSupportException.class)
    public FuncResultEntity supportException(OpenApiPluginSupportException openApiPluginSupportException) {
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
        return FuncResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public FuncResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return FuncResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public FuncResultEntity exception(Exception exception) {
        log.warn("exception:", exception);
        ExceptionUtil.printErrorInfo(exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return FuncResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    public static FuncResultEntity handlerException(Exception e) {
        FuncExceptionHandler exceptionHandler = SpringUtils.getBean(FuncExceptionHandler.class);
        if (e instanceof BindException) {
            return exceptionHandler.bindException((BindException) e);
        } else if (e instanceof MethodArgumentNotValidException) {
            return exceptionHandler.validException((MethodArgumentNotValidException) e);
        } else if (e instanceof OpenApiArgumentException) {
            return exceptionHandler.argumentException((OpenApiArgumentException) e);
        } else if (e instanceof OpenApiFuncException) {
            return exceptionHandler.openApiFuncException((OpenApiFuncException) e);
        } else if (e instanceof OpenApiPluginSupportException) {
            return exceptionHandler.supportException((OpenApiPluginSupportException) e);
        } else if (e instanceof FinhubException) {
            return exceptionHandler.finhubException((FinhubException) e);
        } else if (e instanceof OpenApiBindException) {
            return exceptionHandler.openApiBindException((OpenApiBindException) e);
        }
        return exceptionHandler.exception(e);
    }

}
