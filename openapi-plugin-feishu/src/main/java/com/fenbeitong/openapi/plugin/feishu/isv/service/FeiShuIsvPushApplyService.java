package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuApprovalService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuPushApplyService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @author lizhen
 */
@Slf4j
@ServiceAspect
@Service
public class FeiShuIsvPushApplyService extends AbstractFeiShuPushApplyService {

    @Autowired
    private FeiShuIsvApprovalService feiShuIsvApprovalService;
    @Override
    protected AbstractFeiShuApprovalService getFeiShuApprovalService() {
        return feiShuIsvApprovalService;
    }

    @Override
    protected int getOpenType() {
        return OpenType.FEISHU_ISV.getType();
    }
}
