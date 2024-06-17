package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YunzhijiaApplyCancelHandler  implements ITaskHandler {
    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_APPROVE_CANCEL;
    }

    @Override
    public TaskResult execute(Task task) {
        return null;
    }
}
