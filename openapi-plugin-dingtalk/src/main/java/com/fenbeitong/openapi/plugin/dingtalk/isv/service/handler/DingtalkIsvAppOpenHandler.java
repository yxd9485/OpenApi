package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler;

import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 首次开通
 * @author lizhen
 * @date 2020/6/2
 */
@Component
@Slf4j
public class DingtalkIsvAppOpenHandler implements ITaskHandler {

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Override
    public TaskType getTaskType() {
        return TaskType.DINGTALK_ISV_APP_OPEN;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
        dingtalkIsvCompanyAuthService.activateSuite(corpId);
        dingtalkIsvCompanyAuthService.companyAuth(corpId);
        return TaskResult.SUCCESS;
    }

}
