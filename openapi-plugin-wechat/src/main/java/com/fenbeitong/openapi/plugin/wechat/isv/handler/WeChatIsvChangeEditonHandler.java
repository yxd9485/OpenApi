package com.fenbeitong.openapi.plugin.wechat.isv.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyDefinitionService;
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
public class WeChatIsvChangeEditonHandler implements ITaskHandler {

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private  WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WECHAT_ISV_CHANGE_EDITON;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
        weChatIsvCompanyAuthService.companyChangeEditon(corpId);
        return TaskResult.SUCCESS;
    }

}
