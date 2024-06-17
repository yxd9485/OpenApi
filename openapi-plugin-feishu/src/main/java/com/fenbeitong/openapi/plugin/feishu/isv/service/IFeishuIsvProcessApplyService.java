package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;

/**
 * 飞书审批
 *
 * @author xiaohai
 * @date 2022/02/28
 */
public interface IFeishuIsvProcessApplyService {

    /**
     * 处理钉钉任务
     *
     * @param task         任务
     * @param status       审批单状态
     * @return 处理结果
     */
    TaskResult processApply(Task task,  String status) ;
}
