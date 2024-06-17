package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DubboVoucherServiceImpl</p>
 * <p>Description: dubbo凭证服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 7:38 PM
 */
@Component
@DubboService(timeout = 15000)
public class DubboVoucherServiceImpl extends BaseVoucherServiceImpl {

    @Override
    protected void handlerException(Exception e) {
        handlerDubboException(e);
    }
}
