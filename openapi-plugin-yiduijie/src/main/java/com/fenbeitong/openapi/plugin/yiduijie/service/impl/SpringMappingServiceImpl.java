package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: DubboMappingServiceImpl</p>
 * <p>Description: spring映射服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 11:15 AM
 */
@ServiceAspect
@Service("springMappingService")
public class SpringMappingServiceImpl extends BaseMappingServiceImpl {

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
