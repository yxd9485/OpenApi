package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataMediumType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;

/**
 * @author lizhen
 */
public interface IOpenSyncBizDataMediumTaskHandler {
    /**
     * 获取任务类型
     * @return
     */
    OpenSyncBizDataMediumType getTaskType();

    /**
     * 任务执行方法
     * @param task
     * @return
     */
    TaskResult execute(OpenSyncBizDataMedium task);
}
