package com.fenbeitong.openapi.plugin.feishu.eia.util;

import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuCompanyAuthService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaCompanyAuthService;
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
public class FeiShuEiaHttpUtils extends AbstractFeiShuHttpUtils {

    @Autowired
    private FeiShuEiaCompanyAuthService feiShuEiaCompanyAuthService;

    @Override
    protected AbstractFeiShuCompanyAuthService getFeiShuCompanyAuthService() {
        return feiShuEiaCompanyAuthService;
    }
}
