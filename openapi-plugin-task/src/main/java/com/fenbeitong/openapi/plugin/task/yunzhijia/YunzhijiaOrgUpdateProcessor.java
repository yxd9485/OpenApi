package com.fenbeitong.openapi.plugin.task.yunzhijia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.handler.YunzhijiaOrgAddHandler;
import com.fenbeitong.openapi.plugin.yunzhijia.handler.YunzhijiaOrgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YunzhijiaOrgUpdateProcessor extends YunzhijiaOrgHandler implements ITaskProcessor {

    @Autowired
    private YunzhijiaOrgAddHandler yunzhijiaOrgAddHandler;

    @Autowired
    private TaskConfig taskConfig;

    @Override
    public Integer getTaskType() {
        return TaskType.YUNZHIJIA_ORG_DEPT_MODIFY.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        TaskResult taskResult = yunzhijiaOrgAddHandler.execute(FinhubTaskUtils.convert2Task(task));
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
