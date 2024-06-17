package com.fenbeitong.openapi.plugin.wechat.isv.service.job;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信isv任务处理
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvJobService extends AbstractJobService {

    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.WECHAT_ISV_ORG_DEPT_CREATE.getKey());
        taskType.add(TaskType.WECHAT_ISV_ORG_DEPT_REMOVE.getKey());
        taskType.add(TaskType.WECHAT_ISV_ORG_DEPT_MODIFY.getKey());
        taskType.add(TaskType.WECHAT_ISV_CHANGE_AUTH.getKey());
        taskType.add(TaskType.WECHAT_ISV_CREATE_USER.getKey());
        taskType.add(TaskType.WECHAT_ISV_UPDATE_USER.getKey());
        taskType.add(TaskType.WECHAT_ISV_DELETE_USER.getKey());
        taskType.add(TaskType.WECHAT_ISV_CHANGE_EDITON.getKey());
        taskType.add(TaskType.WECHAT_ISV_PAY_SUCCESS.getKey());
        taskType.add(TaskType.WECHAT_ISV_BATCH_JOB_RESULT.getKey());
        return taskType;
    }
}