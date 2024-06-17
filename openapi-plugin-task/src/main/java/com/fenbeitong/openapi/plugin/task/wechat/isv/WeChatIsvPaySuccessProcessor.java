package com.fenbeitong.openapi.plugin.task.wechat.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
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
public class WeChatIsvPaySuccessProcessor extends AbstractTaskProcessor {

    @Autowired
    private WeChatIsvOpenPayService weChatIsvOpenPayService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_ISV_PAY_SUCCESS.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
//        String dataId = task.getDataId();
//        return weChatIsvOpenPayService.callbackOrder(dataId);
        return TaskProcessResult.success("success");
    }

}
