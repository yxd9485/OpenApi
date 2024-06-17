package com.fenbeitong.openapi.plugin.wechat.eia.service.callback;

import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.common.service.AbstractOpenapiService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.logger.service.CallbackLogService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalInfoEvent;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatDataCallbackDecryptBody;
import com.fenbeitong.openapi.plugin.wechat.common.enums.MsgEventIgnoreType;
import com.fenbeitong.openapi.plugin.wechat.common.exception.AesException;
import com.fenbeitong.openapi.plugin.wechat.common.util.SHA1;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatEiaCallbackTagConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.dao.WeChatTokenKeyDao;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.WeChatEiaChangeContactCallbackBody;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatTokenKey;
import com.fenbeitong.openapi.plugin.wechat.eia.service.company.WeChatEiaCompanyAuthService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.job.WeChatEiaTaskService;
import com.fenbeitong.openapi.plugin.wechat.eia.util.WXBizMsgCrypt;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fenbeitong.openapi.plugin.wechat.eia.enums.WeChatApplyStatus.Pass;

/**
 * 微信回调
 * Created by dave.hansins on 19/12/9.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaCallbackService extends AbstractOpenapiService {

    @Autowired
    private WeChatEiaTaskService weChatEiaTaskService;
    @Autowired
    private CallbackLogService callbackLogService;
    @Autowired
    private PluginCorpDefinitionDao corpConfigDao;
    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    private WeChatTokenKeyDao weChatTokenKeyDao;
    @Autowired
    WXBizMsgCrypt wxBizMsgCrypt;
    @Autowired
    private WeChatEiaCompanyAuthService weChatEiaCompanyAuthService;
    @Autowired
    private ITaskService taskService;

    /**
     * 微信回调验证
     */
    public String verify(String corpId, String corpEncodingAesKey, String token, String signature, String nonce, String echostr, String timestamp) {
        try {
            WXBizMsgCrypt wxcpt = getWXBizMsgCrypt(corpId, corpEncodingAesKey, token);
            if (wxcpt == null) {
                return "";
            }
            return wxcpt.verifyURL(signature, timestamp, nonce, echostr);
        } catch (Exception e) {
            //验证URL失败，错误原因请查看异常
            log.info("解密异常,验证URL失败: {}", JsonUtils.toJson(e.getMessage()));
            return "";
        }
    }

    /**
     * 回调
     */
    public void callback(String corpId, String corpEncodingAesKey, String token, String postData, String signature, String nonce, String timestamp) {
        WXBizMsgCrypt wxcpt = getWXBizMsgCrypt(corpId, corpEncodingAesKey, token);
        if (wxcpt == null) {
            return;
        }
        //解析xml数据
        String verifyMsg = null;
        try {
            verifyMsg = wxcpt.decryptMsg(signature, timestamp, nonce, postData);
        } catch (AesException e) {
            e.printStackTrace();
        }
        WeChatDataCallbackDecryptBody event = (WeChatDataCallbackDecryptBody) XmlUtil.xml2Object(verifyMsg, WeChatDataCallbackDecryptBody.class);
        if (event == null) {
            log.info("转换数据异常");
            return;
        }
        WeChatTokenKey weChatTokenKey = getWeChatTokenInfoByExample(event.getToUserName());
        if (MsgEventIgnoreType.IGNORE_ALL.getKey().equals(weChatTokenKey.getCorpIgnoreEvent()) &&
                (WeChatEiaCallbackTagConstant.WECHAT_CHANGE_CONTACT.equals(event.getEvent()) ||
                        WeChatEiaCallbackTagConstant.WECHAT_CHANGE_CONTACT.equals(event.getInfoType()))) {
            log.info("企业已配置回调忽略，丢弃");
            return;
        }
        //解析数据创建任务
        if (!StringUtils.isBlank(event.getInfoType())) {
            event.setEvent(event.getInfoType());
        }
        if (StringUtils.isBlank(event.getEvent())){
            log.info("当前回调事件为空 , 忽略");
            return;
        }
        switch (event.getEvent()) {
            case WeChatEiaCallbackTagConstant.WECHAT_APPLY_INSTANCE:
                WeChatApprovalInfoEvent weChatApprovalInfoEvent = (WeChatApprovalInfoEvent) XmlUtil.xml2Object(verifyMsg, WeChatApprovalInfoEvent.class);
                createWechatApply(weChatApprovalInfoEvent);
                break;
            case WeChatEiaCallbackTagConstant.WECHAT_CHANGE_CONTACT:
                WeChatEiaChangeContactCallbackBody weChatEiaChangeContactCallbackBody = (WeChatEiaChangeContactCallbackBody) XmlUtil.xml2Object(verifyMsg, WeChatEiaChangeContactCallbackBody.class);
                genContactTask(weChatEiaChangeContactCallbackBody);
                break;
            case WeChatEiaCallbackTagConstant.WECHAT_SUITE_TICKET:
                weChatEiaCompanyAuthService.saveSuiteTicket(verifyMsg);
                break;
            case WeChatEiaCallbackTagConstant.WECHAT_CREATE_AUTH:
                //不初始化信息，初始化信息实施在实施平台配置，该过程让用户完成授权，便于实施查询加密企业三方id，完成实施平台配置，后续重发授权码更新表里授权码数据。
                break;
            case WeChatEiaCallbackTagConstant.WECHAT_RESET_PERMANENT_CODE:
                weChatEiaCompanyAuthService.companyAuthWithAuthCode(verifyMsg);
                break;

            default:
                log.info("event不存在");
        }
        callbackLogService.log(corpId, event.getEvent(), JsonUtils.toJson(event));
    }

    /**
     * 创建企业微信审批单任务
     */
    private void createWechatApply(WeChatApprovalInfoEvent event) {
        // 只处理审批单结束，并且审批通过的单子
        if (Pass.getState() != event.getApprovalInfo().getSpStatus()) {
            log.info("WeChatApprovalInfoEvent非审批完成状态，不处理，{}", event);
            return;
        }
        //获取企业微信公司ID获取具体公司配置信息
        String weChatCorpId = event.getToUserName();
        //企业微信审批模板编号，相对应钉钉审批的process_code,根据模板来识别是否是分贝通审批单
        String templateId = event.getApprovalInfo().getTemplateId();
        PluginCorpDefinition pluginCorpDefinition = corpConfigDao.getCorpByThirdCorpId(weChatCorpId);
        log.info("根据企业微信ID查询企业信息 {}", JsonUtils.toJson(pluginCorpDefinition));
        ThirdApplyDefinition thirdApplyDefinition = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(templateId);
        log.info("根据企业微信ID查询企业审批信息 {}", JsonUtils.toJson(thirdApplyDefinition));
        if (thirdApplyDefinition == null || !pluginCorpDefinition.getAppId().equals(thirdApplyDefinition.getAppId())) {
            log.info("非分贝通审批单, 跳过 spNo: {}", templateId);
            return;
        }
        weChatEiaTaskService.createWeChatTask(weChatCorpId, event.getApprovalInfo().getSpNo(), TaskType.WECHAT_EIA_APPROVAL_CREATE.getKey(), JsonUtils.toJson(event), event.getApprovalInfo().getApplyTime());

    }

    private WXBizMsgCrypt getWXBizMsgCrypt(String corpId, String corpEncodingAesKey, String token) {
        try {
            return new WXBizMsgCrypt(token, corpEncodingAesKey, corpId);
        } catch (AesException e) {
            return null;
        }
    }


    /**
     * 根据企业微信传递参数查询
     *
     * @param signature
     * @param timeStamp
     * @param nonce
     * @param echoStr
     * @return
     * @throws AesException
     */
    public WeChatTokenKey getWeChatCorpInfo(String signature, String timeStamp, String nonce, String echoStr) throws AesException {
        //获取企业微信token表所有数据
        Example example = new Example(WeChatTokenKey.class);
        example.createCriteria().andIsNotNull("corpId");
        List<WeChatTokenKey> weChatTokenKeyList = weChatTokenKeyDao.listByExample(example);
        //根据token进行签名比对
        WeChatTokenKey qywxTokenKey0 = new WeChatTokenKey();
        for (WeChatTokenKey qywxTokenKey : weChatTokenKeyList) {
            String corpToken = qywxTokenKey.getCorpToken();
            //验证哪个企业签名数据比对数据，获取符合企业信息的数据
            String decryptSignature = SHA1.getSHA1(corpToken, timeStamp, nonce, echoStr);
            if (decryptSignature.equals(signature)) {//验证签名成功
                qywxTokenKey0 = qywxTokenKey;
            }
        }
        return qywxTokenKey0;
    }


    /**
     * 根据企业微信公司ID查询token相关信息
     *
     * @param corpId
     * @return
     */
    public WeChatTokenKey getWeChatTokenInfoByExample(String corpId) {
        Example example = new Example(WeChatTokenKey.class);
        example.createCriteria().andEqualTo("corpId", corpId);
        return weChatTokenKeyDao.getByExample(example);
    }


    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }


    /**
     * 通讯录变更任务
     *
     * @param weChatEiaChangeContactCallbackBody
     */
    public void genContactTask(WeChatEiaChangeContactCallbackBody weChatEiaChangeContactCallbackBody) {
        String changeType = weChatEiaChangeContactCallbackBody.getChangeType();
        switch (changeType) {
            case WeChatEiaCallbackTagConstant.WECHAT_UPDATE_USER:
            case WeChatEiaCallbackTagConstant.WECHAT_CREATE_USER:
                weChatEiaChangeContactCallbackBody.setChangeType(WeChatEiaCallbackTagConstant.WECHAT_EIA_CREATE_OR_UPDATE_USER);
                initGenUserTask(weChatEiaChangeContactCallbackBody);
                break;

            case WeChatEiaCallbackTagConstant.WECHAT_DELETE_USER:
                weChatEiaChangeContactCallbackBody.setChangeType(WeChatEiaCallbackTagConstant.WECHAT_EIA_DELETE_USER);
                initGenUserTask(weChatEiaChangeContactCallbackBody);
                break;

            case WeChatEiaCallbackTagConstant.WECHAT_ORG_DEPT_MODIFY:
            case WeChatEiaCallbackTagConstant.WECHAT_ORG_DEPT_CREATE:
                weChatEiaChangeContactCallbackBody.setChangeType(WeChatEiaCallbackTagConstant.WECHAT_EIA_CREATE_OR_UPDATE_DEPT);
                initGenDepartmentTask(weChatEiaChangeContactCallbackBody);
                break;

            case WeChatEiaCallbackTagConstant.WECHAT_ORG_DEPT_REMOVE:
                weChatEiaChangeContactCallbackBody.setChangeType(WeChatEiaCallbackTagConstant.WECHAT_EIA_REMOVE_DEPT);
                initGenDepartmentTask(weChatEiaChangeContactCallbackBody);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化人员task数据
     */
    private void initGenUserTask(WeChatEiaChangeContactCallbackBody weChatEiaChangeContactCallbackBody) {
        String eventTime = StringUtils.obj2str(weChatEiaChangeContactCallbackBody.getCreateTime());
        String corpId = weChatEiaChangeContactCallbackBody.getToUserName();
        String userIds = weChatEiaChangeContactCallbackBody.getUserId();
        String eventType = weChatEiaChangeContactCallbackBody.getChangeType();
        String[] userIdArray = userIds.split(",");
        for (String userId : userIdArray) {
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", corpId);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("DataId", userId);
            eventMsg.put("DataContent", JsonUtils.toJson(weChatEiaChangeContactCallbackBody));
            List<String> taskList = new ArrayList<>();
            if (TaskType.WECHAT_EIA_DELETE_USER.getKey().equals(eventType)) {
                taskList.add(TaskType.WECHAT_EIA_DELETE_USER.getKey());
            } else {
                taskList = Lists.newArrayList(TaskType.WECHAT_EIA_CREATE_OR_UPDATE_USER.getKey());
            }
            taskService.genTask(eventMsg, taskList);
        }
    }


    /**
     * 初始化组织机构task数据
     */
    private void initGenDepartmentTask(WeChatEiaChangeContactCallbackBody weChatEiaChangeContactCallbackBody) {
        String eventTime = StringUtils.obj2str(weChatEiaChangeContactCallbackBody.getCreateTime());
        String corpId = weChatEiaChangeContactCallbackBody.getToUserName();
        String deptCodes = weChatEiaChangeContactCallbackBody.getId();
        String eventType = weChatEiaChangeContactCallbackBody.getChangeType();
        String[] deptCodeArray = deptCodes.split(",");
        for (String deptCode : deptCodeArray) {
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", corpId);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("DataId", deptCode);
            eventMsg.put("DataContent", JsonUtils.toJson(weChatEiaChangeContactCallbackBody));
            List<String> taskList = new ArrayList<>();
            if (TaskType.WECHAT_EIA_REMOVE_DEPT.getKey().equals(eventType)) {
                taskList.add(TaskType.WECHAT_EIA_REMOVE_DEPT.getKey());
            } else {
                taskList = Lists.newArrayList(TaskType.WECHAT_EIA_CREATE_OR_UPDATE_DEPT.getKey());
            }
            taskService.genTask(eventMsg, taskList);
        }
    }

}
