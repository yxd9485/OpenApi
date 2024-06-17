package com.fenbeitong.openapi.plugin.yunzhijia.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YunzhijiaOrgUpdateHandler extends YunzhijiaOrgHandler implements ITaskHandler {

    @Autowired
    private YunzhijiaOrgAddHandler yunzhijiaOrgAddHandler;

    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_ORG_DEPT_MODIFY;
    }

    @Override
    public TaskResult execute(Task task) {
        return yunzhijiaOrgAddHandler.execute(task);
    }
}
