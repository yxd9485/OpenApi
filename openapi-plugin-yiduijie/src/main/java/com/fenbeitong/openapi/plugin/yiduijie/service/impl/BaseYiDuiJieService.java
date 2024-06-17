package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieResultEntity;
import com.fenbeitong.openapi.plugin.yiduijie.handler.YiDuiJieExceptionHandler;

import java.util.List;

/**
 * <p>Title: BaseYiDuiJieService</p>
 * <p>Description: 易对接基础接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 3:24 PM
 */
public abstract class BaseYiDuiJieService {

    @SuppressWarnings("all")
    protected void checkReqParam(List reqList, Class<?>... groups) {
        reqList.forEach(req -> {
            OpenApiBindException bindException = ValidatorUtils.checkValid(req, groups);
            if (bindException != null) {
                handlerException(bindException);
            }
        });
    }

    protected void handlerException(Exception e) {

    }

    protected void handlerDubboException(Exception e) {
        YiDuiJieResultEntity yiDuiJieResultEntity = YiDuiJieExceptionHandler.handlerException(e);
        throw new FinhubException(yiDuiJieResultEntity.getCode(), yiDuiJieResultEntity.getMsg());
    }
}
