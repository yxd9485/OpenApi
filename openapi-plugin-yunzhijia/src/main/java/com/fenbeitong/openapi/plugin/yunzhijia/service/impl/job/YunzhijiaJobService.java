package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.job;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.job.service.IJobService;
import com.fenbeitong.openapi.plugin.support.task.dao.TaskDao;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.task.YunzhijiaTaskService;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static java.util.stream.Collectors.groupingBy;

@ServiceAspect
@Service
@Slf4j
/**
 * 不同插件的job调度单独处理，每个插件只处理自己的任务数据，不需要所有插件集中扫描，必秒数据量过大
 * 导致执行时间过长,在不同插件的具体任务执行服务中根据不同业务处理自己的任务数据
 */
public class YunzhijiaJobService extends AbstractJobService {
    @Autowired
    YunzhijiaTaskService yunzhijiaTaskService;
    @Autowired
    TaskDao taskDao;

    @Override
    protected List<String> getTaskType() {
        List<String> taskTypes = Lists.newArrayList();
        taskTypes.add(TaskType.YUNZHIJIA_APPROVE_CREATE.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_APPROVE_CANCEL.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_USER_ADD_ORG.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_USER_MODIFY_ORG.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_USER_LEAVE_ORG.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_ORG_DEPT_CREATE.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_ORG_DEPT_MODIFY.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_ORG_DEPT_REMOVE.getKey());
        taskTypes.add(TaskType.YUNZHIJIA_ORG_DEPT_LEADER_MODIFY.getKey());
        return taskTypes;
    }
}
