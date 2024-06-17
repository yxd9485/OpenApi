package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DubboConfigServiceImpl</p>
 * <p>Description: dubbo配置修改</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 3:08 PM
 */
@Component
@DubboService(timeout = 15000)
public class DubboConfigServiceImpl extends BaseConfigServiceImpl {

    @Override
    protected void handlerException(Exception e) {
        handlerDubboException(e);
    }
}
