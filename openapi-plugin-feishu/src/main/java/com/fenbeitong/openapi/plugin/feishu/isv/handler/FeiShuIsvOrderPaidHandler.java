package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackOrderPaidDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvOrderService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 应用商店应用购买
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvOrderPaidHandler implements ITaskHandler {

    @Autowired
    private FeiShuIsvOrderService feiShuIsvOrderService;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_ISV_ORDER_PAID;
    }

    @Override
    public TaskResult execute(Task task) {
        String dataContent = task.getDataContent();
        log.info("feishu isv processOrderPaid, 开始处理订单，FeiShuIsvCallbackOrderPaidDTO={}", dataContent);
        FeiShuIsvCallbackOrderPaidDTO feiShuIsvCallbackOrderPaidDTO = JsonUtils.toObj(dataContent, FeiShuIsvCallbackOrderPaidDTO.class);
        TaskResult taskResult = feiShuIsvOrderService.processOrderPaid(feiShuIsvCallbackOrderPaidDTO);
        return taskResult;
    }

}
