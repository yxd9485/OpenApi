package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lizhen
 * @date 2020/8/27
 */
@ServiceAspect
@Service
public class DingtalkJobServiceImpl extends AbstractJobService {
    /**
     * 获取各自业务分支的taskType
     *
     * @return
     */
    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_DEPT.getKey());
        taskType.add(TaskType.DINGTALK_EIA_DELETE_DEPT.getKey());
        taskType.add(TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_USER.getKey());
        taskType.add(TaskType.DINGTALK_EIA_DELETE_USER.getKey());
        taskType.add(TaskType.DINGTALK_EIA_BPMS_INSTANCE_CHANGE.getKey());
        taskType.add(TaskType.YIDA_BPMS_INSTANCE_CHANGE.getKey());
        return taskType;
    }
}
