package com.fenbeitong.openapi.plugin.task.utils;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.luastar.swift.base.utils.ObjUtils;

import java.util.concurrent.TimeUnit;

/**
 * <p>Title: FinhubTaskUtils</p>
 * <p>Description: FinhubTaskUtils</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2022/3/30 6:02 PM
 */
public class FinhubTaskUtils {

    public static TaskProcessResult convert2FinhubTaskResult(TaskResult taskResult) {
        boolean isSuccess = taskResult.isSuccess();
        return isSuccess ? TaskProcessResult.success("success") : TaskProcessResult.fail(taskResult.getMsg());
    }

    public static Task convert2Task(FinhubTask finhubTask) {
        Task task = new Task();
        task.setId(finhubTask.getId());
        TaskType taskType = TaskType.getByCode(finhubTask.getTaskType());
        task.setTaskType(taskType == null ? null : taskType.getKey());
        task.setCorpId(finhubTask.getCompanyId());
        task.setDataId(finhubTask.getDataId());
        task.setDataContent(finhubTask.getDataContent());
        return task;
    }

    public static Long getSleepSeconds(FinhubTask task) {
        int retryCount = ObjUtils.toInteger(task.getExecuteNum(), 0);
        if (retryCount > 29) {
            retryCount = 29;
        }

        long baseSleepSeconds = TimeUnit.MINUTES.toSeconds(5L);
        long sleepSeconds = baseSleepSeconds * (long)(1 << retryCount + 1);
        long maxSleepSeconds = TimeUnit.DAYS.toSeconds(30L);
        if (sleepSeconds > maxSleepSeconds) {
            sleepSeconds = maxSleepSeconds;
        }

        return sleepSeconds;
    }

}
