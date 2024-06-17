package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DubboDepartmentServiceImpl</p>
 * <p>Description: dubbo同步记账部门服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 6:16 PM
 */
@Component
@DubboService(timeout = 15000)
public class DubboDepartmentServiceImpl extends BaseDepartmentServiceImpl {

    @Override
    protected void handlerException(Exception e) {
        handlerDubboException(e);
    }
}
