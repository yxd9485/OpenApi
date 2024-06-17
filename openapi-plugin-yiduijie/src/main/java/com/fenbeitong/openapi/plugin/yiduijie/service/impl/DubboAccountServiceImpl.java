package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DubboAccountServiceImpl</p>
 * <p>Description: dubbo科目服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 6:10 PM
 */
@Component
@DubboService(timeout = 15000)
public class DubboAccountServiceImpl extends BaseAccountServiceImpl {

    @Override
    protected void handlerException(Exception e) {
        handlerDubboException(e);
    }
}
