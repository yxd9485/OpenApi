package com.fenbeitong.openapi.plugin.task.yunzhijia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.handler.YunzhijiaEmployeeAddHandler;
import com.fenbeitong.openapi.plugin.yunzhijia.handler.YunzhijiaEmployeeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YunzhijiaEmployeeUpdateProcessor extends YunzhijiaEmployeeHandler implements ITaskProcessor {


    @Autowired
    private YunzhijiaEmployeeAddHandler yunzhijiaEmployeeAddHandler;

    @Autowired
    private TaskConfig taskConfig;

    @Override
    public Integer getTaskType() {
        return TaskType.YUNZHIJIA_USER_MODIFY_ORG.getCode();
    }


    @Override
    public TaskProcessResult process(FinhubTask task) {
        TaskResult taskResult = yunzhijiaEmployeeAddHandler.execute(FinhubTaskUtils.convert2Task(task));
        return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
    }

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}
