package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvOpenPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lizhen
 * @date 2020/9/15
 */
@Component
@Slf4j
public class WeChatIsvPaySuccessHandler implements ITaskHandler {

    @Autowired
    private WeChatIsvOpenPayService weChatIsvOpenPayService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_ISV_PAY_SUCCESS;
    }

    @Override
    public TaskResult execute(Task task) {
//        String dataId = task.getDataId();
//        return weChatIsvOpenPayService.callbackOrder(dataId);
        return TaskResult.ABORT;
    }

}
