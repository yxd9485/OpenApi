package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;

/**
 * @author lizhen
 */
public interface IOpenSyncBizDataTaskHandler {
    /**
     * 获取任务类型
     * @return
     */
    OpenSyncBizDataType getTaskType();

    /**
     * 任务执行方法
     * @param task
     * @return
     */
    TaskResult execute(OpenSyncBizData task);
}
