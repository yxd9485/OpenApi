package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.support.logger.service.CallbackLogService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.fenbeitong.openapi.plugin.wechat.common.exception.AesException;
import com.fenbeitong.openapi.plugin.wechat.isv.aes.WXIsvBizMsgCrypt;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import com.fenbeitong.openapi.plugin.wechat.isv.service.job.WeChatIsvTaskService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业微信ISV回调service
 * Created by lizhen on 2020/3/20.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvCallbackService {

    @Value("${wechat.isv.token}")
    private String token;
    @Value("${wechat.isv.encoding-aes-key}")
    private String encodingAesKey;

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private WeChatIsvTaskService weChatIsvTaskService;
    @Autowired
    private CallbackLogService callbackLogService;

    @Autowired
    private ITaskService taskService;

    /**
     * 处理command回调
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param xmlBody
     * @throws AesException
     */
    public void commandCallback(String signature, String timestamp, String nonce, String xmlBody) {
        WeChatIsvCallbackBody weChatIsvCallbackBody = (WeChatIsvCallbackBody) XmlUtil.xml2Object(xmlBody, WeChatIsvCallbackBody.class);
        WXIsvBizMsgCrypt wxcpt = null;
        String decryptMsg = null;
        try {
            wxcpt = WXIsvBizMsgCrypt.getInstance(weChatIsvCallbackBody.getToUserName(), encodingAesKey);
            decryptMsg = wxcpt.decryptMsg(token, signature, timestamp, nonce, xmlBody);
        } catch (AesException e) {
            log.error("commandCallback Error", e);
        }
        log.info("wechat commandCallback msg:{}", decryptMsg);
        WeChatIsvCommandCallbackBody weChatIsvCommandCallbackBody = (WeChatIsvCommandCallbackBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvCommandCallbackBody.class);
        String infoType = weChatIsvCommandCallbackBody.getInfoType();
        switch (infoType) {
            case WeChatIsvConstant.WECHAT_SUITE_TICKET:
                weChatIsvCompanyAuthService.saveSuiteTicket(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_CREATE_AUTH:
                weChatIsvCompanyAuthService.companyAuth(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_CHANGE_AUTH:
                initChangeAuthTask(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_CANCEL_AUTH:
                weChatIsvCompanyAuthService.cancelAuth(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_CHANGE_CONTACT:
                genContactTask(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_PAY_FOR_APP_SUCCESS:
            case WeChatIsvConstant.WECHAT_CHANGE_EDITON:
                initChangeEditionTask(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_OPENPAY_SUCCESS:
                initPaySuccessTask(decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_ISV_BATCH_JOB_RESULT:
                initContactIdTranslateTask(decryptMsg);
                break;

            default:
                log.info("不处理的回调：{}", infoType);
        }

    }

    /**
     * 通讯录变更任务
     *
     * @param decryptMsg
     */
    public void genContactTask(String decryptMsg) {
        WeChatIsvChangeContactCallbackBody weChatIsvChangeContactCallbackBody = (WeChatIsvChangeContactCallbackBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvChangeContactCallbackBody.class);
        String changeType = weChatIsvChangeContactCallbackBody.getChangeType();
        String authCorpId = weChatIsvChangeContactCallbackBody.getAuthCorpId();
        callbackLogService.log(authCorpId, changeType, decryptMsg);
        switch (changeType) {
            case WeChatIsvConstant.WECHAT_CREATE_USER:
                weChatIsvChangeContactCallbackBody.setChangeType(WeChatIsvConstant.WECHAT_ISV_CREATE_USER);
                initGenUserTask(weChatIsvChangeContactCallbackBody, decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_UPDATE_USER:
                weChatIsvChangeContactCallbackBody.setChangeType(WeChatIsvConstant.WECHAT_ISV_UPDATE_USER);
                initGenUserTask(weChatIsvChangeContactCallbackBody, decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_DELETE_USER:
                weChatIsvChangeContactCallbackBody.setChangeType(WeChatIsvConstant.WECHAT_ISV_DELETE_USER);
                initGenUserTask(weChatIsvChangeContactCallbackBody, decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_ORG_DEPT_CREATE:
                weChatIsvChangeContactCallbackBody.setChangeType(WeChatIsvConstant.WECHAT_ISV_ORG_DEPT_CREATE);
                initGenDepartmentTask(weChatIsvChangeContactCallbackBody, decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_ORG_DEPT_MODIFY:
                weChatIsvChangeContactCallbackBody.setChangeType(WeChatIsvConstant.WECHAT_ISV_ORG_DEPT_MODIFY);
                initGenDepartmentTask(weChatIsvChangeContactCallbackBody, decryptMsg);
                break;

            case WeChatIsvConstant.WECHAT_ORG_DEPT_REMOVE:
                weChatIsvChangeContactCallbackBody.setChangeType(WeChatIsvConstant.WECHAT_ISV_ORG_DEPT_REMOVE);
                initGenDepartmentTask(weChatIsvChangeContactCallbackBody, decryptMsg);
                break;

        }
    }

    public void initGenUserTask(WeChatIsvChangeContactCallbackBody weChatIsvChangeContactCallbackBody, String dataContent) {
        String eventType = weChatIsvChangeContactCallbackBody.getChangeType();
        String corpId = weChatIsvChangeContactCallbackBody.getAuthCorpId();
        String eventTime = weChatIsvChangeContactCallbackBody.getTimeStamp();
        String userId = weChatIsvChangeContactCallbackBody.getUserID();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("UserId", Lists.newArrayList(userId));
        weChatIsvTaskService.genWeChatIsvUserTask(eventMsg);
    }

    public void initGenDepartmentTask(WeChatIsvChangeContactCallbackBody weChatIsvChangeContactCallbackBody, String dataContent) {
        String eventType = weChatIsvChangeContactCallbackBody.getChangeType();
        String corpId = weChatIsvChangeContactCallbackBody.getAuthCorpId();
        String eventTime = weChatIsvChangeContactCallbackBody.getTimeStamp();
        String id = weChatIsvChangeContactCallbackBody.getId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DeptId", Lists.newArrayList(id));
        weChatIsvTaskService.genQywxDepartmentTask(eventMsg);
    }

    public void initChangeAuthTask(String decryptMsg) {
        log.info("wechat isv changeAuth, 接收到changeAuth");
        WeChatIsvCompanyChangeAuthDecryptBody weChatIsvCompanyAuthDecryptBody = (WeChatIsvCompanyChangeAuthDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvCompanyChangeAuthDecryptBody.class);
        String corpId = weChatIsvCompanyAuthDecryptBody.getAuthCorpId();
        String eventType = weChatIsvCompanyAuthDecryptBody.getInfoType();
        String eventTime = StringUtils.obj2str(System.currentTimeMillis());
        String id = corpId;
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DeptId", Lists.newArrayList(id));
        callbackLogService.log(corpId, eventType, decryptMsg);
        weChatIsvTaskService.genWeChatIsvChangeAuthTask(eventMsg);

    }

    public void initChangeEditionTask(String decryptMsg) {
        log.info("wechat isv changeEditon, 接收到changeEditon");
        WeChatIsvCompanyChangeEditonDecryptBody weChatIsvCompanyEditonDecryptBody = (WeChatIsvCompanyChangeEditonDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvCompanyChangeEditonDecryptBody.class);
        String corpId = weChatIsvCompanyEditonDecryptBody.getPaidCorpId();
        String eventType = TaskType.WECHAT_ISV_CHANGE_EDITON.getKey();
        String eventTime = StringUtils.obj2str(System.currentTimeMillis());
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", corpId);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        taskList.add(TaskType.WECHAT_ISV_CHANGE_EDITON.getKey());
        taskService.genTask(eventMsg, taskList);
    }

    public void initContactIdTranslateTask(String decryptMsg) {
        log.info("wechat isv initContactIdTranslateTask, batch_job_result");
        WeChatIsvBatchJobResultBody weChatIsvBatchJobResultBody = (WeChatIsvBatchJobResultBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvBatchJobResultBody.class);
        String corpId = weChatIsvBatchJobResultBody.getAuthCorpId();
        String eventType = TaskType.WECHAT_ISV_BATCH_JOB_RESULT.getKey();
        String eventTime = StringUtils.obj2str(System.currentTimeMillis());
        String jobId = weChatIsvBatchJobResultBody.getBatchJob().getJobId();
        String jobType = weChatIsvBatchJobResultBody.getBatchJob().getJobType();
        if (WeChatIsvConstant.WECHAT_ISV_JOB_TYPE_CONTACT_ID_TRANSLATE.equals(jobType)) {
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", corpId);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("DataId", jobId);
            eventMsg.put("DataContent", decryptMsg);
            List<String> taskList = new ArrayList<>();
            taskList.add(eventType);
            taskService.genTask(eventMsg, taskList);
        }
    }

    public void initPaySuccessTask(String decryptMsg) {
        log.info("wechat isv paySuccess, 接收到paySuccess");
        WeChatIsvPaySuccessDecryptBody weChatIsvPaySuccessDecryptBody = (WeChatIsvPaySuccessDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvPaySuccessDecryptBody.class);
        String corpId = weChatIsvPaySuccessDecryptBody.getBuyerCorpId();
        String eventType = TaskType.WECHAT_ISV_PAY_SUCCESS.getKey();
        String eventTime = StringUtils.obj2str(System.currentTimeMillis());
        String dataId = weChatIsvPaySuccessDecryptBody.getOrderId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", dataId);
        eventMsg.put("DataContent", decryptMsg);
        List<String> taskList = new ArrayList<>();
        taskList.add(TaskType.WECHAT_ISV_PAY_SUCCESS.getKey());
        taskService.genTask(eventMsg, taskList);
    }

}
