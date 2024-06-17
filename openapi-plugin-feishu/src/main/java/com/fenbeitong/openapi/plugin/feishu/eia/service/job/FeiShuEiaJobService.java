package com.fenbeitong.openapi.plugin.feishu.eia.service.job;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * feishu eia任务处理
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaJobService extends AbstractJobService {
    /**
     * 获取各自业务分支的taskType
     * @return
     */
    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.FEISHU_EIA_ORG_DEPT_CREATE.getKey());
        taskType.add(TaskType.FEISHU_EIA_ORG_DEPT_UPDATE.getKey());
        taskType.add(TaskType.FEISHU_EIA_ORG_DEPT_DELETE.getKey());
        taskType.add(TaskType.FEISHU_EIA_DELETE_USER.getKey());
        taskType.add(TaskType.FEISHU_EIA_UPDATE_USER.getKey());
        taskType.add(TaskType.FEISHU_EIA_CREATE_USER.getKey());
        taskType.add(TaskType.FEISHU_EIA_APPROVAL_CREATE.getKey());
        taskType.add(TaskType.FEISHU_EIA_APPROVAL_REVERTED.getKey());
        return taskType;
    }


}