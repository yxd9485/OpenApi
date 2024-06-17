package com.fenbeitong.openapi.plugin.daoyiyun.handler;

import com.finhub.framework.core.SpringUtils;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunResultEntity;
import com.fenbeitong.openapi.plugin.daoyiyun.util.DaoYiYunResponseUtils;
import com.fenbeitong.openapi.plugin.support.exception.ExceptionUtil;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

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
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.daoyiyun"})
public class DaoYiYunExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public DaoYiYunResultEntity bindException(BindException bindException) {
        log.warn("bindException:", bindException);
        List<ObjectError> allErrors = bindException.getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DaoYiYunResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public DaoYiYunResultEntity missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.warn("missingServletRequestParameterException:", missingServletRequestParameterException);
        String msg = missingServletRequestParameterException.getMessage();
        return DaoYiYunResponseUtils.error(RespCode.ARGUMENT_ERROR, msg);
    }

    @ResponseBody
    @ExceptionHandler(OpenApiBindException.class)
    private DaoYiYunResultEntity openApiBindException(OpenApiBindException e) {
        log.warn("bindException:", e);
        List<ObjectError> allErrors = e.getErrorList();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DaoYiYunResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DaoYiYunResultEntity validException(MethodArgumentNotValidException validException) {
        log.warn("validException:", validException);
        List<ObjectError> allErrors = validException.getBindingResult().getAllErrors();
        List<String> errorList = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return DaoYiYunResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public DaoYiYunResultEntity validException(ConstraintViolationException constraintViolationException) {
        log.warn("constraintViolationException:", constraintViolationException);
        Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
        List<String> errorList = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return DaoYiYunResponseUtils.error(RespCode.ARGUMENT_ERROR, String.join("\n", errorList));
    }

    @ResponseBody
    @ExceptionHandler(OpenApiArgumentException.class)
    public DaoYiYunResultEntity argumentException(OpenApiArgumentException argumentException) {
        log.warn("argumentException:", argumentException);
        return DaoYiYunResponseUtils.error(argumentException.getCode(), argumentException.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(FinhubException.class)
    public DaoYiYunResultEntity finhubException(FinhubException finhubException) {
        log.warn("finhubException:", finhubException);
        int code = finhubException.getCode();
        String msg = finhubException.getMessage();
        return DaoYiYunResponseUtils.error(code, msg);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public DaoYiYunResultEntity exception(Exception exception) {
        log.warn("exception:", exception);
        ExceptionUtil.printErrorInfo(exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return DaoYiYunResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

    public static DaoYiYunResultEntity handlerException(Exception e) {
        DaoYiYunExceptionHandler exceptionHandler = SpringUtils.getBean(DaoYiYunExceptionHandler.class);
        if (e instanceof BindException) {
            return exceptionHandler.bindException((BindException) e);
        } else if (e instanceof MethodArgumentNotValidException) {
            return exceptionHandler.validException((MethodArgumentNotValidException) e);
        } else if (e instanceof OpenApiArgumentException) {
            return exceptionHandler.argumentException((OpenApiArgumentException) e);
        } else if (e instanceof FinhubException) {
            return exceptionHandler.finhubException((FinhubException) e);
        } else if (e instanceof OpenApiBindException) {
            return exceptionHandler.openApiBindException((OpenApiBindException) e);
        }
        return exceptionHandler.exception(e);
    }

}
