package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackApprovalDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackBaseReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackDepartmentDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackEmployeeDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackAppStatusChangeDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackOrderPaidDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.handler.FeiShuIsvAppOpenHandler;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskDataSrc;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书回调处理service
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvCallbackService {

    @Value("${feishu.isv.encryptKey}")
    private String encryptKey;

    @Value("${feishu.isv.verificationToken}")
    private String verificationToken;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private FeiShuIsvCompanyAuthService feiShuIsvCompanyAuthService;

    @Autowired
    private FeiShuIsvMessageService feiShuIsvMessageService;

    @Autowired
    private FeiShuIsvAppOpenHandler feiShuIsvAppOpenHandler;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    public String commandCallback(String encryptMsg) {
//        FeiShuIsvCallbackEncryptReqDTO feiShuIsvCallbackEncryptReqDTO = JsonUtils.toObj(encryptMsg, FeiShuIsvCallbackEncryptReqDTO.class);
//        String encrypt = feiShuIsvCallbackEncryptReqDTO.getEncrypt();
//        if (StringUtils.isBlank(encrypt)) {
//            return null;
//        }
//        String decryptMsg = null;
//        try {
//            decryptMsg = AesUtils.decryptByCbc(encrypt, encryptKey);
//        } catch (Exception e) {
//            log.error("【feishu callback】aes解密失败", e);
//            throw new OpenApiFeiShuException(FeiShuResponseCode.AES_ERROR);
//        }
        String decryptMsg = encryptMsg;
        FeiShuCallbackBaseReqDTO feiShuCallbackBaseReqDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackBaseReqDTO.class);
        String token = feiShuCallbackBaseReqDTO.getToken();
        if (!verificationToken.equals(token)) {
            log.info("【feishu callback】 token不正确，丢弃");
            return null;
        }
        String type = feiShuCallbackBaseReqDTO.getType();
        if (!StringUtils.isBlank(type)) {
            // 验证请求
            if (FeiShuConstant.CALLBACK_TYPE_VERIFYCATION.equals(type)) {
                return urlVerificationCallback(feiShuCallbackBaseReqDTO);
            }
            if (FeiShuConstant.CALLBACK_TYPE_EVENT.equals(type)) {
                String eventType = feiShuCallbackBaseReqDTO.getEvent().getType();
                String tenantKey = feiShuCallbackBaseReqDTO.getEvent().getTenantKey();
                if ("2ee83c762dcf1657".equals(tenantKey)) {
                    return null;
                }
                switch (eventType) {
                    case FeiShuConstant.APP_OPEN_EVENT:
                        initGenAppAuthTask(decryptMsg, TaskType.FEISHU_ISV_APP_OPEN.getKey());
                        break;
                    case FeiShuConstant.APP_STATUS_CHANGE_EVENT:
                        initGenAppAuthTask(decryptMsg, TaskType.FEISHU_ISV_APP_STATUS_CHANGE.getKey());
                        break;
                    case FeiShuConstant.USER_ADD_EVENT:
                        initGenUserTask(decryptMsg, TaskType.FEISHU_ISV_CREATE_USER.getKey());
                        break;
                    case FeiShuConstant.USER_UPDATE_EVENT:
                        initGenUserTask(decryptMsg, TaskType.FEISHU_ISV_UPDATE_USER.getKey());
                        break;
                    case FeiShuConstant.USER_LEAVE_EVENT:
                        initGenUserTask(decryptMsg, TaskType.FEISHU_ISV_DELETE_USER.getKey());
                        break;
                    case FeiShuConstant.DEPT_ADD_EVENT:
                        initGenDepartmentTask(decryptMsg, TaskType.FEISHU_ISV_ORG_DEPT_CREATE.getKey());
                        break;
                    case FeiShuConstant.DEPT_UPDATE_EVENT:
                        initGenDepartmentTask(decryptMsg, TaskType.FEISHU_ISV_ORG_DEPT_UPDATE.getKey());
                        break;
                    case FeiShuConstant.DEPT_DELETE_EVENT:
                        initGenDepartmentTask(decryptMsg, TaskType.FEISHU_ISV_ORG_DEPT_DELETE.getKey());
                        break;
                    case FeiShuConstant.APP_TICKET_EVENT:
                        feiShuIsvCompanyAuthService.saveAppTicket(decryptMsg);
                        break;
                    case FeiShuConstant.CONTACT_SCOPE_CHANGE:
                        initGenAppAuthTask(decryptMsg, TaskType.FEISHU_ISV_APP_STATUS_CHANGE.getKey());
                        break;
                    case FeiShuConstant.P2P_CHAT_CREATE:
                        feiShuIsvMessageService.processP2PChatCreate(decryptMsg);
                        break;
                    case FeiShuConstant.MESSAGE:
                        feiShuIsvMessageService.processMessage(decryptMsg);
                        break;
                    case FeiShuConstant.ADD_USER_TO_CHAT:
                        feiShuIsvMessageService.processAddUserToChat(decryptMsg);
                        break;
                    case FeiShuConstant.ADD_BOT:
                        feiShuIsvMessageService.processAddBot(decryptMsg);
                        break;
                    case FeiShuConstant.ORDER_PAID:
                        initOrderPaidTask(decryptMsg);
                        break;
                    case FeiShuConstant.APPROVAL_INSTANCE_EVENT:
                        initGenApprovalTask(decryptMsg);
                        break;
                    default:
                        log.info("不处理的事件");
                        break;
                }
            }
        }
        return null;
    }

    /**
     * 验证回调
     *
     * @param feiShuCallbackBaseReqDTO
     * @return
     */
    private String urlVerificationCallback(FeiShuCallbackBaseReqDTO feiShuCallbackBaseReqDTO) {
        String challenge = feiShuCallbackBaseReqDTO.getChallenge();
        Map<String, Object> map = new HashMap<>(1);
        map.put("challenge", challenge);
        return JsonUtils.toJson(map);
    }

    /**
     * 企业授权、状态变更
     *
     * @param decryptMsg
     */
    private void initGenAppAuthTask(String decryptMsg, String eventType) {
        FeiShuIsvCallbackAppStatusChangeDTO feiShuIsvCallbackAppStatusChangeDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackAppStatusChangeDTO.class);
        String eventTime = feiShuIsvCallbackAppStatusChangeDTO.getTs();
        String corpId = feiShuIsvCallbackAppStatusChangeDTO.getEvent().getTenantKey();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", corpId);
        eventMsg.put("DataContent", decryptMsg);
        //开通应用的任务先运行
        if (TaskType.FEISHU_ISV_APP_OPEN.getKey().equals(eventType)) {
            runAuthTask(eventMsg, eventType);
        } else {
            taskService.genTask(eventMsg, Lists.newArrayList(eventType));
        }
    }

    private void runAuthTask(Map<String, Object> eventMsg, String eventType) {
        String corpId = (String) eventMsg.get("CorpId");
        String eventTime = String.valueOf(eventMsg.get("TimeStamp"));
        String dataId = (String) eventMsg.get("DataId");
        String dataContent = (String) eventMsg.get("DataContent");
        Task task = new Task();
        task.setCorpId(corpId);
        task.setDataId(dataId);
        task.setTaskType(eventType);
        task.setDataSrc(TaskDataSrc.PULL.getKey());
        task.setDataContent(JsonUtils.toJson(eventMsg));
        task.setEventTime(NumericUtils.obj2long(eventTime));
        task.setExecuteMax(3);
        task.setDataContent(dataContent);
        try {
            feiShuIsvAppOpenHandler.execute(task);
        } catch (Exception e) {
            log.error("执行失败，入库", e);
            taskService.genTask(eventMsg, Lists.newArrayList(eventType));
        }
    }

    /**
     * 初始化人员task数据
     *
     * @param decryptMsg
     * @param eventType
     */
    private void initGenUserTask(String decryptMsg, String eventType) {
        FeiShuCallbackEmployeeDTO feiShuCallbackEmployeeDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackEmployeeDTO.class);
        String eventTime = feiShuCallbackEmployeeDTO.getTs();
        String corpId = feiShuCallbackEmployeeDTO.getEvent().getTenantKey();
        String userId = feiShuCallbackEmployeeDTO.getEvent().getOpenId();
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            return;
        }
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", userId);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        if (TaskType.FEISHU_ISV_DELETE_USER.getKey().equals(eventType)) {
            taskList.add(TaskType.FEISHU_ISV_DELETE_USER.getKey());
        } else {
            taskList = Lists.newArrayList(TaskType.FEISHU_ISV_CREATE_USER.getKey(), TaskType.FEISHU_ISV_UPDATE_USER.getKey());
        }
        taskService.genTask(eventMsg, taskList);
    }


    /**
     * 初始化组织机构task数据
     *
     * @param decryptMsg
     * @param eventType
     */
    private void initGenDepartmentTask(String decryptMsg, String eventType) {
        FeiShuCallbackDepartmentDTO feiShuCallbackDepartmentDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackDepartmentDTO.class);
        String eventTime = feiShuCallbackDepartmentDTO.getTs();
        String corpId = feiShuCallbackDepartmentDTO.getEvent().getTenantKey();
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            return;
        }
        String deptCode = feiShuCallbackDepartmentDTO.getEvent().getOpenDepartmentId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", deptCode);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        if (TaskType.FEISHU_ISV_ORG_DEPT_DELETE.getKey().equals(eventType)) {
            taskList.add(TaskType.FEISHU_ISV_ORG_DEPT_DELETE.getKey());
        } else {
            taskList = Lists.newArrayList(TaskType.FEISHU_ISV_ORG_DEPT_CREATE.getKey(), TaskType.FEISHU_ISV_ORG_DEPT_UPDATE.getKey());
        }
        taskService.genTask(eventMsg, taskList);
    }

    /**
     * 初始化应用商店应用购买task数据
     *
     * @param decryptMsg
     */
    private void initOrderPaidTask(String decryptMsg) {
        FeiShuIsvCallbackOrderPaidDTO feiShuIsvCallbackOrderPaidDTO = JsonUtils.toObj(decryptMsg, FeiShuIsvCallbackOrderPaidDTO.class);
        String eventTime = feiShuIsvCallbackOrderPaidDTO.getTs();
        String corpId = feiShuIsvCallbackOrderPaidDTO.getEvent().getTenantKey();
        String orderId = feiShuIsvCallbackOrderPaidDTO.getEvent().getOrderId();
        String eventType = TaskType.FEISHU_ISV_ORDER_PAID.getKey();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", orderId);
        eventMsg.put("DataContent", decryptMsg);
        taskService.genTask(eventMsg, Lists.newArrayList(eventType));
    }


    /**
     * 生成审批创建任务
     *
     * @param decryptMsg
     */
    private void initGenApprovalTask(String decryptMsg) {
        FeiShuCallbackApprovalDTO feiShuCallbackApprovalDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackApprovalDTO.class);
        if (ObjectUtils.isEmpty(feiShuCallbackApprovalDTO)) {
            log.info("飞书创建审批表单接收格式错误 ");
            return;
        }
        String eventTime = feiShuCallbackApprovalDTO.getTs();
        String corpId = feiShuCallbackApprovalDTO.getEvent().getTenantKey();
        String instanceCode = feiShuCallbackApprovalDTO.getEvent().getInstanceCode();
        String status = feiShuCallbackApprovalDTO.getEvent().getStatus();
        String taskType = null;
        if (FeiShuConstant.APPROVAL_INSTANCE_STATUS_APPROVED.equals(status)) {
            taskType = TaskType.FEISHU_ISV_APPROVAL_CREATE.getKey();
        } else if (FeiShuConstant.APPROVAL_INSTANCE_STATUS_REVERTED.equals(status)) {
            taskType = TaskType.FEISHU_ISV_APPROVAL_REVERTED.getKey();
        } else if (FeiShuConstant.APPROVAL_INSTANCE_STATUS_REJECTED.equals(status)) {
            taskType = TaskType.FEISHU_ISV_APPROVAL_CREATE.getKey();
        } else {
            log.info("飞书创建审批表单, 审批单状态未结束，丢弃");
            return;
        }
        //具体审批事件类型，包括新增和撤销
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", taskType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", instanceCode);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        taskList.add(taskType);
        taskService.genTask(eventMsg, taskList);
    }


}
