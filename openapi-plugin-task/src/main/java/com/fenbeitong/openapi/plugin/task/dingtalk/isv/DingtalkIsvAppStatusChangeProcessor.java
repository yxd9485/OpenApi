package com.fenbeitong.openapi.plugin.task.dingtalk.isv;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 应用启动、停用事件
 *
 * @author lizhen
 * @date 2020/6/2
 */
@Component
@Slf4j
public class DingtalkIsvAppStatusChangeProcessor extends AbstractTaskProcessor {

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Override
    public Integer getTaskType() {
        return TaskType.DINGTALK_ISV_APP_STATUS_CHANGE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String corpId = task.getCompanyId();
        String dataContent = task.getDataContent();
        Map<String, Object> plainTextJson = JsonUtils.toObj(dataContent, Map.class);
        String eventType = StringUtils.obj2str(plainTextJson.get("EventType"));
        //变更授权范围
        if (DingtalkCallbackTagConstant.CHANGE_AUTH.equals(eventType)) {
            dingtalkIsvCompanyAuthService.companyAuth(corpId);
            dingtalkIsvEmployeeService.syncOrgEmployee(corpId);
        }
        //停用
        if (DingtalkCallbackTagConstant.ORG_MICRO_APP_STOP.equals(eventType) || DingtalkCallbackTagConstant.SUITE_RELIEVE.equals(eventType)) {
            dingtalkIsvCompanyAuthService.companyCancelAuth(corpId);
        }
        //启用
        if (DingtalkCallbackTagConstant.ORG_MICRO_APP_RESTORE.equals(eventType)) {
            dingtalkIsvCompanyAuthService.companyAuth(corpId);
        }
        return TaskProcessResult.success("success");
    }

}
