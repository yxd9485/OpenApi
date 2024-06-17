package com.fenbeitong.openapi.plugin.task.dingtalk.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 首次开通
 *
 * @author lizhen
 * @date 2020/6/2
 */
@Component
@Slf4j
public class DingtalkIsvAppOpenProcessor extends AbstractTaskProcessor {

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Override
    public Integer getTaskType() {
        return TaskType.DINGTALK_ISV_APP_OPEN.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String corpId = task.getCompanyId();
        dingtalkIsvCompanyAuthService.activateSuite(corpId);
        dingtalkIsvCompanyAuthService.companyAuth(corpId);
        return TaskProcessResult.success("success");
    }

}
