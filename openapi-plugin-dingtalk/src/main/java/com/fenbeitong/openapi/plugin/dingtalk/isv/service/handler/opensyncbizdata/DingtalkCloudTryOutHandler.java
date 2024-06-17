package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvTryOutDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lizhen
 */
@Component
@Slf4j
public class DingtalkCloudTryOutHandler implements IOpenSyncBizDataTaskHandler {

    @Autowired
    IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;


    @Override
    public OpenSyncBizDataType getTaskType() {
        return OpenSyncBizDataType.DINGTALK_ISV_TRY_OUT;
    }

    @Override
    public TaskResult execute(OpenSyncBizData task) {
        String bizData = task.getBizData();
        DingtalkIsvTryOutDTO dingtalkIsvTryOutDTO = JsonUtils.toObj(bizData, DingtalkIsvTryOutDTO.class);
        dingtalkIsvCompanyAuthService.tryout(dingtalkIsvTryOutDTO);
        return TaskResult.SUCCESS;
    }

}
