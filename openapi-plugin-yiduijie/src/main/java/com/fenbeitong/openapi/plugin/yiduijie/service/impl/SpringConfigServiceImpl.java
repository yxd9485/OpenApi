package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: SpringConfigServiceImpl</p>
 * <p>Description: spring配置服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 3:10 PM
 */
@ServiceAspect
@Service("springConfigService")
public class SpringConfigServiceImpl extends BaseConfigServiceImpl {

    @Override
    protected void handlerException(Exception e) {
        if (e instanceof OpenApiBindException) {
            throw (OpenApiBindException) e;
        }
        if (e instanceof OpenApiYiDuiJieException) {
            throw (OpenApiYiDuiJieException) e;
        }
    }
}
