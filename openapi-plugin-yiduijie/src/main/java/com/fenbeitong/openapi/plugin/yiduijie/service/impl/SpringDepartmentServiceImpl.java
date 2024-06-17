package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: SpringDepartmentServiceImpl</p>
 * <p>Description: spring记账部门同步服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 6:17 PM
 */
@ServiceAspect
@Service("springDepartmentService")
public class SpringDepartmentServiceImpl extends BaseDepartmentServiceImpl {

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
