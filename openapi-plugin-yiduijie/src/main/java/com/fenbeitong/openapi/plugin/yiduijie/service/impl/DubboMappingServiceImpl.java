package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DubboMappingServiceImpl</p>
 * <p>Description: dubbo映射服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 11:15 AM
 */
@Component
@DubboService(timeout = 15000)
public class DubboMappingServiceImpl extends BaseMappingServiceImpl {

    @Override
    protected void handlerException(Exception e) {
        handlerDubboException(e);
    }
}
