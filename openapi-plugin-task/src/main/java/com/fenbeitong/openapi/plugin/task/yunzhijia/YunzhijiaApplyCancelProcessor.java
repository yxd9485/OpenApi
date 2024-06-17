package com.fenbeitong.openapi.plugin.task.yunzhijia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YunzhijiaApplyCancelProcessor extends AbstractTaskProcessor {
    @Override
    public Integer getTaskType() {
        return TaskType.YUNZHIJIA_APPROVE_CANCEL.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        return null;
    }
}
