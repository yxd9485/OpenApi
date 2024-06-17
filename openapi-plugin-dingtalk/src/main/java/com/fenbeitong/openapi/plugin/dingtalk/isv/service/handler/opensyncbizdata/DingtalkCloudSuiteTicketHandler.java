package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/15
 */
@ServiceAspect
@Service
public class DingtalkCloudSuiteTicketHandler implements IOpenSyncBizDataTaskHandler {

    @Autowired
    IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Override
    public OpenSyncBizDataType getTaskType() {
        return OpenSyncBizDataType.DINGTALK_ISV_SUITE_TICKET;
    }

    @Override
    public TaskResult execute(OpenSyncBizData task) {
        String bizData = task.getBizData();
        Map<String, Object> data = JsonUtils.toObj(bizData, Map.class);
        String suiteTicket = StringUtils.obj2str(data.get("suiteTicket"));
        dingtalkIsvCompanyAuthService.saveSuiteTicket(suiteTicket);
        return TaskResult.SUCCESS;
    }
}
