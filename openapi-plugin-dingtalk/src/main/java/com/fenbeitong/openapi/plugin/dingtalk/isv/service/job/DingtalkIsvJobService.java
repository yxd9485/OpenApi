package com.fenbeitong.openapi.plugin.dingtalk.isv.service.job;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * dingtalk isv任务处理
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvJobService extends AbstractJobService {
    /**
     * 获取各自业务分支的taskType
     * @return
     */
    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.DINGTALK_ISV_CREATE_USER.getKey());
        taskType.add(TaskType.DINGTALK_ISV_UPDATE_USER.getKey());
        taskType.add(TaskType.DINGTALK_ISV_DELETE_USER.getKey());
        taskType.add(TaskType.DINGTALK_ISV_CREATE_ORG_DEPT.getKey());
        taskType.add(TaskType.DINGTALK_ISV_UPDATE_ORG_DEPT.getKey());
        taskType.add(TaskType.DINGTALK_ISV_DELETE_ORG_DEPT.getKey());
        taskType.add(TaskType.DINGTALK_ISV_APP_OPEN.getKey());
        taskType.add(TaskType.DINGTALK_ISV_APP_STATUS_CHANGE.getKey());
        return taskType;
    }


}