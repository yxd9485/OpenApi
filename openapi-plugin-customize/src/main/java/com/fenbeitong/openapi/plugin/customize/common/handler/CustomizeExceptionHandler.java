package com.fenbeitong.openapi.plugin.customize.common.handler;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.func.handler.FuncExceptionHandler;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>Title: CustomizeExceptionHandler</p>
 * <p>Description: 定制模块异常处理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/20 4:47 PM
 */
@Slf4j
@ControllerAdvice(basePackages = {"com.fenbeitong.openapi.plugin.customize"})
public class CustomizeExceptionHandler extends FuncExceptionHandler {

    @Autowired
    private ExceptionRemind exceptionRemind;

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public OpenapiResultEntity exception(Throwable exception) {
        log.error("exception:", exception);
        exceptionRemind.exceptionRemindDingTalk(exception);
        return OpenapiResponseUtils.error(RespCode.ERROR, "系统繁忙，请稍后");
    }

}
