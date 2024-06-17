package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.finhub.common.utils.NumericUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkFlowDirConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessEvent;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessResult;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaCallbackService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeEnum;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lizhen on 2020/8/20.
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkEiaCallbackServiceImpl implements IDingtalkEiaCallbackService {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private DingtalkApplyServiceImpl dingtalkApplyService;

    @Override
    public void dispatch(String eventJson) {
        Map<String, Object> plainTextJson = JsonUtils.toObj(eventJson, Map.class);
        // 回调事件类型
        String eventType = (String) plainTextJson.get("EventType");
        switch (eventType) {
            case DingtalkCallbackTagConstant.USER_ADD_ORG:
            case DingtalkCallbackTagConstant.USER_MODIFY_ORG:
                plainTextJson.put("EventType", TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_USER.getKey());
                initGenUserTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.USER_LEAVE_ORG:
                plainTextJson.put("EventType", TaskType.DINGTALK_EIA_DELETE_USER.getKey());
                initGenUserTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.ORG_DEPT_CREATE:
            case DingtalkCallbackTagConstant.ORG_DEPT_MODIFY:
                plainTextJson.put("EventType", TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_DEPT.getKey());
                initGenDepartmentTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.ORG_DEPT_REMOVE:
                plainTextJson.put("EventType", TaskType.DINGTALK_EIA_DELETE_DEPT.getKey());
                initGenDepartmentTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.BPMS_INSTANCE_CHANGE:
                plainTextJson.put("EventType", TaskType.DINGTALK_EIA_BPMS_INSTANCE_CHANGE.getKey());
                initGenProcessTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.CHECK_URL:
                break;
            default:
                log.info("暂未接入的回调事件， eventType: {}", eventType);
                break;
        }
    }

    /**
     * 初始化人员task数据
     */
    private void initGenUserTask(Map<String, Object> plainTextJson) {
        String corpId = StringUtils.obj2str(plainTextJson.get("CorpId"));
        List<String> userIdList = (List) plainTextJson.get("UserId");
        String eventType = StringUtils.obj2str(plainTextJson.get("EventType"));
        String eventTime = StringUtils.obj2str(plainTextJson.get("TimeStamp"));
        for (String userId : userIdList) {
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", corpId);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("DataId", userId);
            eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
            List<String> taskList = new ArrayList<>();
            if (TaskType.DINGTALK_EIA_DELETE_USER.getKey().equals(eventType)) {
                taskList.add(TaskType.DINGTALK_EIA_DELETE_USER.getKey());
            } else {
                taskList = Lists.newArrayList(TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_USER.getKey());
            }
            taskService.genTask(eventMsg, taskList);
        }
    }


    /**
     * 初始化组织机构task数据
     */
    private void initGenDepartmentTask(Map<String, Object> plainTextJson) {
        String corpId = StringUtils.obj2str(plainTextJson.get("CorpId"));
        List<?> ids = (List<?>) plainTextJson.get("DeptId");
        String eventType = StringUtils.obj2str(plainTextJson.get("EventType"));
        String eventTime = StringUtils.obj2str(plainTextJson.get("TimeStamp"));
        List<Long> deptIds = ids.stream().map(String::valueOf).map(Long::valueOf).collect(Collectors.toList());
        for (Long deptCode : deptIds) {
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", corpId);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("DataId", StringUtils.obj2str(deptCode));
            eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
            List<String> taskList = new ArrayList<>();
            if (TaskType.DINGTALK_EIA_DELETE_DEPT.getKey().equals(eventType)) {
                taskList.add(TaskType.DINGTALK_EIA_DELETE_DEPT.getKey());
            } else {
                taskList = Lists.newArrayList(TaskType.DINGTALK_EIA_CREATE_OR_UPDATE_DEPT.getKey());
            }
            taskService.genTask(eventMsg, taskList);
        }
    }

    private void initGenProcessTask(Map<String, Object> plainTextJson) {
        String type = (String) plainTextJson.get("type");
        String instanceId = (String) plainTextJson.get("processInstanceId");
        String processCode = (String) plainTextJson.get("processCode");
        String corpId = (String) plainTextJson.get("corpId");
        String processResult = (String) plainTextJson.get("result");
        if (!(DingtalkProcessEvent.FINISH.getValue().equals(type))){
            log.info("审批流程未结束，跳过. type: {}", type);
            return;
        }
        PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCorpId(corpId);
        if (dingtalkCorp == null) {
            log.warn("钉钉嵌入版未配置企业，请联系实施 corpId:{}",corpId);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        DingtalkApply apply = dingtalkApplyService.getApplyByCodeAndCompanyId(processCode,dingtalkCorp.getAppId());
        if (apply == null ){
            log.info("未配置三方表单流程编码，请联系实施 corpId:{},companyId:{},processCode:{}",corpId,dingtalkCorp.getAppId(),processCode);
            return;
        }
        if ( DingtalkProcessResult.REFUSE.getValue().equals(processResult)
            && DingtalkFlowDirConstant.FORWARD.equals(ProcessTypeEnum.valueOf(apply.getProcessType()))){
            log.info("正向申请单，不支持拒绝状态,跳过。processType:{},processResult:{}",apply.getProcessType(),processResult);
            return;
        }

        String eventType = (String) plainTextJson.get("EventType");
        Long eventTime = (Long) plainTextJson.get("finishTime");
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", instanceId);
        eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
        taskService.genTask(eventMsg, null);
    }
}
