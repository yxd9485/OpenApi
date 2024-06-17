package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: DefaultEcologyWorkFlowListener</p>
 * <p>Description: 默认泛微审批流监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 1:01 PM
 */
@Primary
@ServiceAspect
@Service
@Slf4j
public class DefaultEcologyWorkFlowListener extends AbstractEcologyWorkFlowListener {

    @Override
    public Integer agreed(WorkflowDTO workflowDto) {
        return null;
    }

    /**
     * 创建审批，标准表单
     * @param companyId
     */
    @Override
    public void createApply(String companyId) {

    }
}
