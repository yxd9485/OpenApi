package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuOrganizationService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 飞书部门service
 *
 * @author lizhen
 * @date 2020/6/2
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaOrganizationService extends AbstractFeiShuOrganizationService {

    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuEiaHttpUtils;
    }

    @Override
    protected AbstractFeiShuEmployeeService getFeiShuEmployeeService() {
        return feiShuEiaEmployeeService;
    }

    @Override
    protected int getOpenType(){
        return OpenType.FEISHU_EIA.getType();
    }

}
