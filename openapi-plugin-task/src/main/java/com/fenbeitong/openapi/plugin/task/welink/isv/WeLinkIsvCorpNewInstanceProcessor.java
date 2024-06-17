package com.fenbeitong.openapi.plugin.task.welink.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvCallbackNewInstanceDTO;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lizhen on 2020/3/27.
 */
@Component
@Slf4j
public class WeLinkIsvCorpNewInstanceProcessor extends AbstractTaskProcessor {

    @Autowired
    private WeLinkIsvCompanyAuthService weLinkIsvCompanyAuthService;

    @Override
    public Integer getTaskType() {
        return TaskType.WELINK_ISV_CORP_NEW_INSTANCE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String corpId = task.getCompanyId();
        String userId = "";
        WeLinkIsvCallbackNewInstanceDTO weLinkIsvCallbackNewInstanceDTO = JsonUtils.toObj(task.getDataContent(), WeLinkIsvCallbackNewInstanceDTO.class);
        WeLinkIsvCallbackNewInstanceDTO.PlatformParams platformParams = weLinkIsvCallbackNewInstanceDTO.getPlatformParams();
        if (platformParams != null) {
            userId = platformParams.getUserId();
        }
        weLinkIsvCompanyAuthService.companyAuthTrial(corpId, userId);
        return TaskProcessResult.success("success");
    }

}
