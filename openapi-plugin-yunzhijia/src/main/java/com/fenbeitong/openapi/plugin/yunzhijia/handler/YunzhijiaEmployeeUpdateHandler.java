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
public class YunzhijiaEmployeeUpdateHandler extends YunzhijiaEmployeeHandler implements ITaskHandler {


    @Autowired
    private YunzhijiaEmployeeAddHandler yunzhijiaEmployeeAddHandler;

    @Override
    public TaskType getTaskType() {
        return TaskType.YUNZHIJIA_USER_MODIFY_ORG;
    }


    @Override
    public TaskResult execute(Task task) {
        return yunzhijiaEmployeeAddHandler.execute(task);
    }
}
