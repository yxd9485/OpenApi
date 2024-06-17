package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkCallbackTagConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessEvent;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessResult;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkApplyServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCallbackService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/9
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvCallbackServiceImpl implements IDingtalkIsvCallbackService {

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private DingtalkApplyServiceImpl dingtalkApplyService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private ITaskService taskService;

    @Override
    public void dispatch(String eventJson) {
        Map<String, Object> plainTextJson = JsonUtils.toObj(eventJson, Map.class);
        // 回调事件类型
        String eventType = (String) plainTextJson.get("EventType");
        switch (eventType) {

            case DingtalkCallbackTagConstant.SUITE_TICKET:
                dingtalkIsvCompanyAuthService.saveSuiteTicket(StringUtils.obj2str(plainTextJson.get("SuiteTicket")));
                break;
            case DingtalkCallbackTagConstant.TMP_AUTH_CODE:
                initGenAppAuthTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.ORG_MICRO_APP_STOP:
            case DingtalkCallbackTagConstant.SUITE_RELIEVE:
                initGenAppChangeStatusTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.ORG_MICRO_APP_RESTORE:
                initGenAppChangeStatusTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.CHANGE_AUTH:
                initGenAppChangeStatusTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.MARKET_BUY:
                initGenAppChangeStatusTask(plainTextJson);
                break;
            case DingtalkCallbackTagConstant.BPMS_INSTANCE_CHANGE:
                initGenProcessTask(plainTextJson);
                break;
//            case DingtalkCallbackTagConstant.USER_MODIFY_ORG:
//                genUserTask(plainTextJson);
//                break;
//            case DingtalkCallbackTagConstant.USER_LEAVE_ORG:
//                genUserTask(plainTextJson);
//                break;
//            case DingtalkCallbackTagConstant.ORG_DEPT_CREATE:
//                genDepartmentTask(plainTextJson);
//                break;
//            case DingtalkCallbackTagConstant.ORG_DEPT_MODIFY:
//                genDepartmentTask(plainTextJson);
//                break;
//            case DingtalkCallbackTagConstant.ORG_DEPT_REMOVE:
//                genDepartmentTask(plainTextJson);
//                break;
            case DingtalkCallbackTagConstant.CHECK_CREATE_SUITE_URL:
            case DingtalkCallbackTagConstant.CHECK_UPDATE_SUITE_URL:
                break;
            default:
                log.info("暂未接入的回调事件， eventType: {}", eventType);
                break;
        }
    }



    /**
     * 企业授权
     *
     * @param plainTextJson
     */
    private void initGenAppAuthTask(Map<String, Object> plainTextJson) {
        String eventTime = StringUtils.obj2str(plainTextJson.get("TimeStamp"));
        String dataId = StringUtils.obj2str(plainTextJson.get("AuthCode"));
        String corpId = StringUtils.obj2str(plainTextJson.get("AuthCorpId"));
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", TaskType.DINGTALK_ISV_APP_OPEN.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", dataId);
        eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
        taskService.genTask(eventMsg, Lists.newArrayList(TaskType.DINGTALK_ISV_APP_OPEN.getKey()));
    }

    /**
     * 授权变更 、启停用
     *
     * @param plainTextJson
     */
    private void initGenAppChangeStatusTask(Map<String, Object> plainTextJson) {
        String eventTime = StringUtils.obj2str(plainTextJson.get("TimeStamp"));
        String dataId = StringUtils.obj2str(plainTextJson.get("AuthCorpId"));
        String corpId = StringUtils.obj2str(plainTextJson.get("AuthCorpId"));
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", TaskType.DINGTALK_ISV_APP_STATUS_CHANGE.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", dataId);
        eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
        taskService.genTask(eventMsg, null);
    }

    /**
     * 三方应用审批
     *
     * @param plainTextJson
     */
    private void initGenProcessTask(Map<String, Object> plainTextJson) {
        String type = (String) plainTextJson.get("type");
        String instanceId = (String) plainTextJson.get("processInstanceId");
        String processCode = (String) plainTextJson.get("processCode");
        String corpId = (String) plainTextJson.get("corpId");
        String processResult = (String) plainTextJson.get("result");
        // 只处理审批单结束，并且审批通过的单子
        if (!(DingtalkProcessEvent.FINISH.getValue().equals(type) && DingtalkProcessResult.AGREE.getValue().equals(processResult))) {
            log.info("三方应用审批非审核通过审批单，跳过. type: {}, result: {}", type, processResult);
            return;
        }
        // 只处理分贝通的审批单
        PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCorpId(corpId);
        DingtalkApply apply = dingtalkApplyService.getAppyByProcessCode(processCode);
        log.info("三方应用审批非分贝通审批单, dingtalkCorp {}", JsonUtils.toJson(dingtalkCorp));
        log.info("三方应用审批非分贝通审批单, apply {}", JsonUtils.toJson(apply));
        if (apply == null || !dingtalkCorp.getAppId().equals(apply.getCompanyId())) {
            log.info("三方应用审批非分贝通审批单, 跳过 processCode: {}", processCode);
            return;
        }
        Long eventTime = (Long) plainTextJson.get("finishTime");
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", TaskType.DINGTALK_ISV_BPMS_INSTANCE_CHANGE.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", instanceId);
        eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
        taskService.genTask(eventMsg,  Lists.newArrayList(TaskType.DINGTALK_ISV_BPMS_INSTANCE_CHANGE.getKey()));
    }


    /**
     * 企业授权
     *
     * @param plainTextJson
     */
    private void initGenMarketBuyTask(Map<String, Object> plainTextJson) {
        String eventTime = StringUtils.obj2str(plainTextJson.get("TimeStamp"));
        String dataId = StringUtils.obj2str(plainTextJson.get("orderId"));
        String corpId = StringUtils.obj2str(plainTextJson.get("buyCorpId"));
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", TaskType.DINGTALK_ISV_APP_OPEN.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", dataId);
        eventMsg.put("DataContent", JsonUtils.toJson(plainTextJson));
        taskService.genTask(eventMsg, Lists.newArrayList(TaskType.DINGTALK_ISV_APP_OPEN.getKey()));
    }
//
//    /**
//     * 初始化人员task数据
//     *
//     * @param plainTextJson
//     * @param eventType
//     */
//    private void initGenUserTask(Map<String, Object> plainTextJson, String eventType) {
//        String corpId = plainTextJson.get()
//        Map<String, Object> eventMsg = new HashMap<>();
//        eventMsg.put("EventType", eventType);
//        eventMsg.put("CorpId", corpId);
//        eventMsg.put("TimeStamp", eventTime);
//        eventMsg.put("UserId", userId);
//        feiShuIsvTaskService.genUserTask(eventMsg);
//    }

}
