package com.fenbeitong.openapi.plugin.task.feishu.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackOrderPaidDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvOrderService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用商店应用购买
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvOrderPaidProcessor extends AbstractTaskProcessor {

    @Autowired
    private FeiShuIsvOrderService feiShuIsvOrderService;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_ISV_ORDER_PAID.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String dataContent = task.getDataContent();
        log.info("feishu isv processOrderPaid, 开始处理订单，FeiShuIsvCallbackOrderPaidDTO={}", dataContent);
        FeiShuIsvCallbackOrderPaidDTO feiShuIsvCallbackOrderPaidDTO = JsonUtils.toObj(dataContent, FeiShuIsvCallbackOrderPaidDTO.class);
        TaskResult taskResult = feiShuIsvOrderService.processOrderPaid(feiShuIsvCallbackOrderPaidDTO);
        return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
    }

}
