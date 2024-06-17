package com.fenbeitong.openapi.plugin.feishu.common.service.apply;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import org.springframework.beans.factory.InitializingBean;

/**
 * 飞书反向审批
 *
 * @author xiaohai
 * @date 2022/07/04
 */
public interface FeishuProcessReverseApplyService extends InitializingBean {

    /**
     * 反向审批
     *
     * @param task         任务
     * @param status       审批单状态
     * @param openType
     * @return 处理结果
     */
    TaskResult reverseProcessApply(Task task,  String status , Integer openType) ;

}
