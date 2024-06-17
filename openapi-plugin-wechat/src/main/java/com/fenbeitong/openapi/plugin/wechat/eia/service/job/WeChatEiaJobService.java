package com.fenbeitong.openapi.plugin.wechat.eia.service.job;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by dave.hansins on 19/12/21.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaJobService extends AbstractJobService {
    @Autowired
    WeChatEiaTaskService weChatEiaTaskService;

    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.WECHAT_EIA_APPROVAL_CREATE.getKey());
        taskType.add(TaskType.WECHAT_EIA_CREATE_OR_UPDATE_DEPT.getKey());
        taskType.add(TaskType.WECHAT_EIA_REMOVE_DEPT.getKey());
        taskType.add(TaskType.WECHAT_EIA_CREATE_OR_UPDATE_USER.getKey());
        taskType.add(TaskType.WECHAT_EIA_DELETE_USER.getKey());
        return taskType;
    }
}
