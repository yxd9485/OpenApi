package com.fenbeitong.openapi.plugin.task.daoyiyun;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.impl.DaoYiYunTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 审批单创建
 *
 * @author lizhen
 */
@Component
@Slf4j
public class DaoYiYunApprovalCreateProcessor extends AbstractTaskProcessor {

    @Autowired
    private DaoYiYunTripApplyServiceImpl daoYiYunTripApplyService;

    @Override
    public Integer getTaskType() {
        //todo 编码
        return TaskType.DAOYIYUN_APPLY_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        String companyId = task.getCompanyId();
        String dataContent = task.getDataContent();
        DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO = JsonUtils.toObj(dataContent, DaoYiYunCallbackBodyDTO.class);
        TaskProcessResult taskProcessResult = daoYiYunTripApplyService.processApply(task, daoYiYunCallbackBodyDTO,
            companyId);
        return taskProcessResult;
    }
}
