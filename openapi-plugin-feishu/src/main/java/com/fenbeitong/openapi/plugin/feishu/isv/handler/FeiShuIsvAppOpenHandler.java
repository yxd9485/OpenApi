package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackAppOpenDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvMessageService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
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
public class FeiShuIsvAppOpenHandler implements ITaskHandler {

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;

    @Autowired
    private FeiShuIsvMessageService feiShuIsvMessageService;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_ISV_APP_OPEN;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
        String dataContent = task.getDataContent();
        FeiShuIsvCallbackAppOpenDTO feiShuIsvCallbackAppOpenDTO = JsonUtils.toObj(dataContent, FeiShuIsvCallbackAppOpenDTO.class);
        // 授权负责人openId
        String openId = feiShuIsvCallbackAppOpenDTO.getEvent().getInstaller().getOpenId();
        feiShuIsvCompanyAuthService.companyAuth(corpId, openId);
        try{
            feiShuIsvMessageService.processInstall(openId, corpId);
        } catch (Exception e) {
            log.error("消息推送失败",e);
        }
        return TaskResult.SUCCESS;
    }

}
