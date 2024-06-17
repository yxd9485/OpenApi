package com.fenbeitong.openapi.plugin.welink.isv.service.job;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * welink isv任务处理
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvJobService extends AbstractJobService {
    /**
     * 获取各自业务分支的taskType
     * @return
     */
    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.WELINK_ISV_CORP_REFRESH_INSTANCE.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_AUTH.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_CANCEL_AUTH.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_EDIT_DEPT.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_DEL_DEPT.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_EDIT_USER.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_DEL_USER.getKey());
        taskType.add(TaskType.WELINK_ISV_CORP_NEW_INSTANCE.getKey());
        return taskType;
    }

}
