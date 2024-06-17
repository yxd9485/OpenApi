package com.fenbeitong.openapi.plugin.feishu.common.service.apply.impl;

import com.fenbeitong.finhub.common.constant.saas.ApplyOrderCategory;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.AbstractApplyReverseService;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeishuProcessReverseApplyService;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyProcessReverseFactory;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 飞书用餐审批处理
 * @author xiaohai
 * @Date 2022/07/04
 */
@Slf4j
@ServiceAspect
@Service
public class FeiShuDinnerReverseApplyServiceImpl extends AbstractApplyReverseService implements FeishuProcessReverseApplyService {

    @Override
    protected String getCategory() {
        return ApplyOrderCategory.DINNER.getCategory();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplyProcessReverseFactory.registerHandler(ProcessTypeConstant.DINNER_REVERSE , this );
    }



}
