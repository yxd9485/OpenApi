package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuScheduleService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author xiaohai
 */
@ServiceAspect
@Service
public class FeiShuEiaScheduleService extends AbstractFeiShuScheduleService {
    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuEiaHttpUtils;
    }


}
