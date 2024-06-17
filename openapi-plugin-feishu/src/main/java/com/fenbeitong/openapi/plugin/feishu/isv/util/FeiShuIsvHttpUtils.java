package com.fenbeitong.openapi.plugin.feishu.isv.util;

import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuCompanyAuthService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 飞书http工具类，用于自动填充accessToken和accessToken失效重试
 *
 * @author lizhen
 * @date 2020/3/22
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvHttpUtils extends AbstractFeiShuHttpUtils {

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;

    @Override
    protected AbstractFeiShuCompanyAuthService getFeiShuCompanyAuthService() {
        return feiShuIsvCompanyAuthService;
    }
}
