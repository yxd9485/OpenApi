package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackApprovalDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackBaseReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackDepartmentDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackEmployeeDTO;
import com.fenbeitong.openapi.plugin.feishu.common.enums.FeishuApprovalStatus;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
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
 * @date 2020/7/7
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaCallbackService {

    @Autowired
    private ITaskService taskService;

    @Autowired
    protected PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

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
        if (ObjectUtils.isEmpty(feiShuCallbackBaseReqDTO)) {
            log.info("【feishu callback】 参数不正确，丢弃 {}", decryptMsg);
            return null;
        }
        String type = feiShuCallbackBaseReqDTO.getType();
        if (!StringUtils.isBlank(type)) {
            // 验证请求
            if (FeiShuConstant.CALLBACK_TYPE_VERIFYCATION.equals(type)) {
                return urlVerificationCallback(feiShuCallbackBaseReqDTO);
            }
            if (FeiShuConstant.CALLBACK_TYPE_EVENT.equals(type)) {
                String appId = feiShuCallbackBaseReqDTO.getEvent().getAppId();
                PluginCorpAppDefinition pluginCorpAppDefinition = pluginCorpAppDefinitionDao.getByCorpId(appId);
                if (pluginCorpAppDefinition == null) {
                    log.info("【feishu callback】 企业信息不存在,corpId={}", appId);
                    return null;
                }
                String eventType = feiShuCallbackBaseReqDTO.getEvent().getType();
                switch (eventType) {
                    case FeiShuConstant.USER_ADD_EVENT:
                        initGenUserTask(decryptMsg, TaskType.FEISHU_EIA_CREATE_USER.getKey());
                        break;
                    case FeiShuConstant.USER_UPDATE_EVENT:
                        initGenUserTask(decryptMsg, TaskType.FEISHU_EIA_UPDATE_USER.getKey());
                        break;
                    case FeiShuConstant.USER_LEAVE_EVENT:
                        initGenUserTask(decryptMsg, TaskType.FEISHU_EIA_DELETE_USER.getKey());
                        break;
                    case FeiShuConstant.DEPT_ADD_EVENT:
                        initGenDepartmentTask(decryptMsg, TaskType.FEISHU_EIA_ORG_DEPT_CREATE.getKey());
                        break;
                    case FeiShuConstant.DEPT_UPDATE_EVENT:
                        initGenDepartmentTask(decryptMsg, TaskType.FEISHU_EIA_ORG_DEPT_UPDATE.getKey());
                        break;
                    case FeiShuConstant.DEPT_DELETE_EVENT:
                        initGenDepartmentTask(decryptMsg, TaskType.FEISHU_EIA_ORG_DEPT_DELETE.getKey());
                        break;
                    case FeiShuConstant.APPROVAL_CREATE_EVENT:
                        initGenApprovalCreateTask(decryptMsg);
                        break;
                    case FeiShuConstant.APPROVAL_REVERT_EVENT:
                        initGenApprovalRevertTask(decryptMsg);
                        break;
                    case FeiShuConstant.APPROVAL_INSTANCE_EVENT:
                        initGenApprovalTask(decryptMsg);
                    default:
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
     * 初始化人员task数据
     *
     * @param decryptMsg
     * @param eventType
     */
    private void initGenUserTask(String decryptMsg, String eventType) {
        FeiShuCallbackEmployeeDTO feiShuCallbackEmployeeDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackEmployeeDTO.class);
        String eventTime = feiShuCallbackEmployeeDTO.getTs();
        String corpId = feiShuCallbackEmployeeDTO.getEvent().getAppId();
        String userId = feiShuCallbackEmployeeDTO.getEvent().getEmployeeId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", userId);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        if (TaskType.FEISHU_EIA_DELETE_USER.getKey().equals(eventType)) {
            taskList.add(eventType);
        } else {
            taskList = Lists.newArrayList(TaskType.FEISHU_EIA_CREATE_USER.getKey(), TaskType.FEISHU_EIA_UPDATE_USER.getKey());
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
        String corpId = feiShuCallbackDepartmentDTO.getEvent().getAppId();
        String deptCode = feiShuCallbackDepartmentDTO.getEvent().getOpenDepartmentId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", deptCode);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        if (TaskType.FEISHU_EIA_ORG_DEPT_DELETE.getKey().equals(eventType)) {
            taskList.add(TaskType.FEISHU_EIA_ORG_DEPT_DELETE.getKey());
        } else {
            taskList = Lists.newArrayList(TaskType.FEISHU_EIA_ORG_DEPT_CREATE.getKey(), TaskType.FEISHU_EIA_ORG_DEPT_UPDATE.getKey());
        }
        taskService.genTask(eventMsg, taskList);
    }

    /**
     * 生成审批创建任务
     *
     * @param decryptMsg
     */
    private void initGenApprovalCreateTask(String decryptMsg) {
        FeiShuCallbackApprovalDTO feiShuCallbackApprovalDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackApprovalDTO.class);
        if (ObjectUtils.isEmpty(feiShuCallbackApprovalDTO)) {
            log.info("飞书创建审批表单接收格式错误 ");
            return;
        }
        String eventTime = feiShuCallbackApprovalDTO.getTs();
        String corpId = feiShuCallbackApprovalDTO.getEvent().getAppId();
        String instanceCode = feiShuCallbackApprovalDTO.getEvent().getInstanceCode();
        //具体审批事件类型，包括新增和撤销
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", FeiShuConstant.APPROVAL_EVENT_CREATE);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", instanceCode);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        taskList.add(TaskType.FEISHU_EIA_APPROVAL_CREATE.getKey());
        taskService.genTask(eventMsg, taskList);
    }


    /**
     * 生成审批撤销任务
     *
     * @param decryptMsg
     */
    private void initGenApprovalRevertTask(String decryptMsg) {
        FeiShuCallbackApprovalDTO feiShuCallbackApprovalDTO = JsonUtils.toObj(decryptMsg, FeiShuCallbackApprovalDTO.class);
        if (ObjectUtils.isEmpty(feiShuCallbackApprovalDTO)) {
            log.info("飞书撤销审批表单接收格式错误 ");
            return;
        }
        String eventTime = feiShuCallbackApprovalDTO.getTs();
        String corpId = feiShuCallbackApprovalDTO.getEvent().getAppId();
        String instanceCode = feiShuCallbackApprovalDTO.getEvent().getInstanceCode();
        //具体审批事件类型，包括和撤销
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", FeiShuConstant.APPROVAL_EVENT_REVERTED);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", instanceCode);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        taskList.add(TaskType.FEISHU_EIA_APPROVAL_REVERTED.getKey());
        taskService.genTask(eventMsg, taskList);
    }

    /**
     * 审批单事件回调处理
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
        String corpId = feiShuCallbackApprovalDTO.getEvent().getAppId();
        String instanceCode = feiShuCallbackApprovalDTO.getEvent().getInstanceCode();
        String status = feiShuCallbackApprovalDTO.getEvent().getStatus();
        String taskType = null;
        if(FeiShuConstant.APPROVAL_INSTANCE_STATUS_APPROVED.equals(status)) {
            taskType = TaskType.FEISHU_EIA_APPROVAL_CREATE.getKey();
        } else if(FeiShuConstant.APPROVAL_INSTANCE_STATUS_REVERTED.equals(status)) {
            taskType = TaskType.FEISHU_EIA_APPROVAL_REVERTED.getKey();
        }else if (FeiShuConstant.APPROVAL_INSTANCE_STATUS_REJECTED.equals(status)) {
            taskType = TaskType.FEISHU_EIA_APPROVAL_CREATE.getKey();
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
