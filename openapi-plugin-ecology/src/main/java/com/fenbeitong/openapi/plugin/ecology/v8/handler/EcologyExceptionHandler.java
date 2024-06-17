package com.fenbeitong.openapi.plugin.ecology.v8.handler;

import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyResponseUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyResultEntity;
import com.fenbeitong.openapi.plugin.support.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>Title: EcologyExceptionHandler</p>
 * <p>Description: 泛微异常处理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/10 4:54 PM
 */
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.ecology"})
@Slf4j
public class EcologyExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public EcologyResultEntity exception(Exception exception) {
        log.error("Exception ", exception);
        ExceptionUtil.printErrorInfo(exception);
        return EcologyResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后重试...");
    }
}
