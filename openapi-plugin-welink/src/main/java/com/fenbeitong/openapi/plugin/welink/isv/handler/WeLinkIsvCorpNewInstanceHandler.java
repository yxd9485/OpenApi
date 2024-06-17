package com.fenbeitong.openapi.plugin.welink.isv.handler;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvCallbackNewInstanceDTO;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lizhen on 2020/3/27.
 */
@Component
@Slf4j
public class WeLinkIsvCorpNewInstanceHandler implements ITaskHandler {

    @Autowired
    private WeLinkIsvCompanyAuthService weLinkIsvCompanyAuthService;

    @Override
    public TaskType getTaskType() {
        return TaskType.WELINK_ISV_CORP_NEW_INSTANCE;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
        String userId = "";
        WeLinkIsvCallbackNewInstanceDTO weLinkIsvCallbackNewInstanceDTO = JsonUtils.toObj(task.getDataContent(), WeLinkIsvCallbackNewInstanceDTO.class);
        WeLinkIsvCallbackNewInstanceDTO.PlatformParams platformParams = weLinkIsvCallbackNewInstanceDTO.getPlatformParams();
        if (platformParams != null) {
            userId = platformParams.getUserId();
        }
        weLinkIsvCompanyAuthService.companyAuthTrial(corpId, userId);
        return TaskResult.SUCCESS;
    }

}
