package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackAppStatusChangeDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 应用启动、停用事件
 *
 * @author lizhen
 * @date 2020/6/2
 */
@Component
@Slf4j
public class FeiShuIsvAppStatusChangeHandler implements ITaskHandler {

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_ISV_APP_STATUS_CHANGE;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
        String dataContent = task.getDataContent();
        FeiShuIsvCallbackAppStatusChangeDTO feiShuIsvCallbackAppStatusChangeDTO = JsonUtils.toObj(dataContent, FeiShuIsvCallbackAppStatusChangeDTO.class);
        String status = feiShuIsvCallbackAppStatusChangeDTO.getEvent().getStatus();
        String type = feiShuIsvCallbackAppStatusChangeDTO.getEvent().getType();
        //变更授权范围
        if (FeiShuConstant.CONTACT_SCOPE_CHANGE.equals(type)) {
            feiShuIsvCompanyAuthService.companyAuth(corpId, null);
        }
        //停用
        if (FeiShuConstant.STATUS_STOP_BY_PLATFORM.equals(status) || FeiShuConstant.STATUS_STOP_BY_TENANT.equals(status)) {
            feiShuIsvCompanyAuthService.companyCancelAuth(corpId);
        }
        //启用
        if (FeiShuConstant.STATUS_START_BY_TENANT.equals(status)) {
            feiShuIsvCompanyAuthService.companyAuth(corpId, null);
        }
        return TaskResult.SUCCESS;
    }

}
