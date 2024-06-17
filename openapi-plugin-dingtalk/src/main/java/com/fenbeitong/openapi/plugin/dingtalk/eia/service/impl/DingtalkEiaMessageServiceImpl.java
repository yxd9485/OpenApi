package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationStatusBarUpdateRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationStatusBarUpdateResponse;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.DingtalkAttendanceApproveImpl;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkMessageUtil;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.*;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkIsvConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.support.common.constant.SaaSApplyCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCompanyClientType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity.DingtalkApproveTask;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.service.IDingtalkApproveTaskService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class DingtalkEiaMessageServiceImpl implements IDingtalkEiaMessageService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private IDingtalkCorpAppService dingtalkCorpAppService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private IDingtalkApproveTaskService dingtalkApproveTaskService;

    @Autowired
    private DingtalkAttendanceApproveImpl dingtalkAttendanceApprove;

    @Autowired
    private OpenSysConfigDao openSysConfigDao;

    @Autowired
    private IApiUserService apiUserService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    /**
     * 向钉钉推送消息
     *
     * @return
     */
    @Override
    public void pushMessage(WebAppPushEvents kafkaPushMsg) {
        log.info("eia向钉钉推送消息内部消息的消息体为：{}", kafkaPushMsg);
        String companyId = kafkaPushMsg.getReceiveCompanyId();
        String userId = kafkaPushMsg.getUserId();
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            log.info("【push信息】eia推送消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
            return;
        }
        //查询企业授权信息
        PluginCorpDefinition pluginCorpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        if (pluginCorpDefinition == null) {
            log.info("【push信息】eia推送非dingtalk eia企业,companyId:{}", companyId);
            return;
        }
        OpenCompanySourceType openCompanySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(companyId);
        Integer clientType = openCompanySourceType.getClientType();
        if (!OpenCompanyClientType.H5.getType().equals(clientType)) {
            log.info("【push信息】eia推送消息推送失败，client_type不正确{}", JsonUtils.toJson(kafkaPushMsg));
            return;
        }
        String corpId = pluginCorpDefinition.getThirdCorpId();
        PluginCorpAppDefinition pluginCorpAppDefinition = dingtalkCorpAppService.getByCorpId(corpId);
        if (pluginCorpAppDefinition == null) {
            log.info("【push信息】eia推送该企业钉钉密钥不存在,companyId:{},corpId:{}", companyId, corpId);
            return;
        }
        String thirdEmployeeId = kafkaPushMsg.getThirdEmployeeId();
        Long thirdAgentId = pluginCorpAppDefinition.getThirdAgentId();
        String msgType = kafkaPushMsg.getMsgType();
        String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_EIA_APP_HOME, corpId);
        thirdEmployeeId = convertUserId(companyId, kafkaPushMsg.getPhoneNum(), corpId, thirdEmployeeId);
        OapiMessageCorpconversationAsyncsendV2Request request = DingtalkMessageUtil.setOaMsg(kafkaPushMsg, thirdEmployeeId, thirdAgentId, uri);
        //判断消息是审批还是订单数据
        if (MessagePushUtils.checkMsgType(msgType) || EventConstant.MSG_TYPE_APPLY_CANCEL.equals(msgType)) {
            Map eventMsgMap = JsonUtils.toObj(kafkaPushMsg.getMsg(), Map.class);
            if (eventMsgMap != null) {
                String applyId = StringUtils.obj2str(eventMsgMap.get("id"));
                if (eventMsgMap != null) {
                    // 钉钉审批记录考勤
                    OpenSysConfig OpenSysConfig = openSysConfigDao.getOpenSysConfigByTypeCode(OpenSysConfigType.DINGTALK_ATTENDANCE_APPROVE.getType(), companyId);
                    if (!ObjectUtils.isEmpty(OpenSysConfig)) {
                        attendanceApprove(applyId, thirdEmployeeId, corpId, MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri), kafkaPushMsg);
                    }
                }
            }
        }
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(msgType)) {
            sendMessage(request, corpId);
        }
    }

    //审批通知处理
    private void applyMessageHandle(OapiMessageCorpconversationAsyncsendV2Request request, String applyId, String userId, String corpId, long agentId) {
        /**
         * 1、推送sass过来的数据
         * 2、查询表中所有历史数据推送给钉钉推送消息将该消息状态设置为已处理
         * 3、修改表中所有的历史数据（修改状态为已处理）
         * 4、新增最新一条消息（消息的最后一条消息为为处理，不会再有下次的消息推送，所以无需处理）
         */
        // 1、推送消息
        long taskId = sendMessage(request, corpId);
        // 2推送历史消息修改状态
        List<DingtalkApproveTask> dingtalkApproveTasks = pushEiaMsgUpdateStatus(applyId, agentId, corpId);
        //3 修改历史数据状态
        dingtalkApproveTaskService.updateMessageStatus(dingtalkApproveTasks);
        // 4、表里新增数据
        DingtalkApproveTask dingtalkApprove = DingtalkApproveTask.builder().approveId(applyId).userId(userId).taskId(taskId).build();
        dingtalkApproveTaskService.insertDingtalkApproveTask(dingtalkApprove);
    }

    //推送历史消息修改状态
    public List<DingtalkApproveTask> pushEiaMsgUpdateStatus(String applyId, long agentId, String corpId) {
        List<DingtalkApproveTask> dingtalkApproveTaskByApproveList = dingtalkApproveTaskService.getDingtalkApproveTaskByStatus(applyId, 0);
        if (dingtalkApproveTaskByApproveList != null && dingtalkApproveTaskByApproveList.size() > 0) {
            //修改所有历史节点为已处理
            dingtalkApproveTaskByApproveList.forEach(task -> {
                //推送修改状态的通知，修改表里审批单状态
                long taskId = task.getTaskId();
                pushMessageUpdateStatus(corpId, agentId, taskId);//推送修改状态消息
            });
        }
        return dingtalkApproveTaskByApproveList;
    }

    //推送修改任务状态信息
    public void pushMessageUpdateStatus(String corpId, long agentId, long taskId) {
        OapiMessageCorpconversationStatusBarUpdateRequest request = DingtalkMessageUtil.setUpdateMsg(taskId, agentId);
        updateStatusBar(request, corpId);
    }

    //action_card消息体
    private OapiMessageCorpconversationAsyncsendV2Request setCardMsg(KafkaPushMsg kafkaPushMsg, long agentId, String thirdEmployeeId, String corpId) {
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(thirdEmployeeId);
        request.setAgentId(agentId);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
        msg.getActionCard().setTitle(title);
        msg.getActionCard().setMarkdown(content);
        msg.getActionCard().setSingleTitle(title);
        msg.setMsgtype("action_card");

        String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_EIA_APP_HOME, corpId);
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        msg.getActionCard().setSingleUrl(messageUrl);
        request.setMsg(msg);
        return request;
    }

    public Long sendMessage(OapiMessageCorpconversationAsyncsendV2Request request, String corpId) {
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        String url = proxyUrl + "/topapi/message/corpconversation/asyncsend_v2";
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiMessageCorpconversationAsyncsendV2Response response;
        try {
            response = client.execute(request, accessToken);
            log.info("eia钉钉发送工作通知完成，参数: corpId: {}，request: {}，result: {}", corpId, JsonUtils.toJson(request), response.getBody());
            return response.getTaskId();
        } catch (ApiException e) {
            log.error("eia钉钉发送工作通知接口异常：{}", e);
        }
        return null;
    }

    public void updateStatusBar(OapiMessageCorpconversationStatusBarUpdateRequest request, String corpId) {
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        String url = proxyUrl + "/topapi/message/corpconversation/status_bar/update";
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiMessageCorpconversationStatusBarUpdateResponse response;
        try {
            response = client.execute(request, accessToken);
            log.info("eia钉钉更新工作通知完成，参数: corpId: {}，request: {}，result: {}", corpId, JsonUtils.toJson(request), response.getBody());
        } catch (ApiException e) {
            log.error("eia钉钉更新工作通知接口异常：{}", e);
        }
    }

    @Async
    public void attendanceApprove(String applyId, String userId, String corpId, String msgUrl, KafkaPushMsg kafkaPushMsg) {
        Map map = JsonUtils.toObj(kafkaPushMsg.getMsg(), Map.class);
        String companyId = kafkaPushMsg.getCompanyId();
        if (!ObjectUtils.isEmpty(map)) {
            String settingType = (String) MapUtils.getValueByExpress(map, "setting_type");
            String viewType = StringUtils.obj2str(map.get("view_type"));
            // 16 代表自定义模板
            if ("16".equals(settingType) && SaaSApplyCode.SaasViewType.SAAS_VIEW_TYPE_APPLYER.equals(viewType)) {
                String accessToken = dingtalkTokenService.getAccessToken(corpId);
                String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
                switch (kafkaPushMsg.getTitle()) {
                    // 完成
                    case DingTalkConstant.custformApplyState.FINISH:
                        dingtalkAttendanceApprove.execute(companyId, applyId, userId, msgUrl, accessToken, proxyUrl);
                        break;
                    case DingTalkConstant.custformApplyState.REVOCATION:
                        dingtalkAttendanceApprove.approveCancel(userId, applyId, accessToken, proxyUrl);
                }
            }
        }
    }

    private String convertUserId(String companyId, String phoneNum, String corpId, String thirdEmployeeId) {
        try {
            OpenThirdScriptConfig freeAccountConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.USER_FREE_LOGIN);
            if ( null == freeAccountConfig ) {
                log.info("未配置开关,不需要转换钉钉id");
                return thirdEmployeeId;
            }
            log.info("配置消息脚本,开始转换三方id");
            String dingtalkUserId = apiUserService.getDingtalkUserIdByPhoneNum(corpId, phoneNum);
            if (StringUtils.isBlank(dingtalkUserId)) {
                log.info("钉钉 userId 为空,返回原有id");
                return thirdEmployeeId;
            }
            return dingtalkUserId;
        } catch (Exception e) {
            log.info("钉钉获取 userId 处理失败:{}", e.getMessage());
            return thirdEmployeeId;
        }
    }
}
