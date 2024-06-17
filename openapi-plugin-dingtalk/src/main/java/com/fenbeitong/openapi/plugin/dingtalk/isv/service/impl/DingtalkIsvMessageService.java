package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationSendbytemplateRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationStatusBarUpdateRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationSendbytemplateResponse;
import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.DingtalkAttendanceApproveImpl;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkMessageUtil;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkIsvConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvMessageService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.support.common.constant.SaaSApplyCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity.DingtalkApproveTask;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.service.IDingtalkApproveTaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/22
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvMessageService implements IDingtalkIsvMessageService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Value("${host.dd_webapp}")
    private String webappHost;

    @Value("${dingtalk.isv.messageTemplateId}")
    private String messageTemplateId;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private IDingtalkApproveTaskService dingtalkApproveTaskService;

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private DingtalkAttendanceApproveImpl dingtalkAttendanceApprove;

    @Autowired
    private OpenSysConfigDao openSysConfigDao;


    /**
     * 向钉钉推送消息
     *
     * @return
     */
    @Override
    public void pushMessage(WebAppPushEvents kafkaPushMsg) {
        log.info("isv接收到消息内部消息的消息体为：{}", kafkaPushMsg);
        String companyId = kafkaPushMsg.getReceiveCompanyId();
        String userId = kafkaPushMsg.getUserId();
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
            return;
        }
        //查询企业授权信息
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            log.info("【push信息】非dingtalk isv企业,companyId:{}", companyId);
            return;
        }
        String corpId = dingtalkIsvCompany.getCorpId();
        String msgType = kafkaPushMsg.getMsgType();
        Long agentid = dingtalkIsvCompany.getAgentid();
        String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_ISV_APP_HOME, corpId);
        OapiMessageCorpconversationSendbytemplateRequest request =
            DingtalkMessageUtil.setTemplateMessage(kafkaPushMsg, kafkaPushMsg.getThirdEmployeeId(), agentid, uri, messageTemplateId);
        //判断消息是审批还是订单数据
        if (MessagePushUtils.checkMsgType(msgType) || EventConstant.MSG_TYPE_APPLY_CANCEL.equals(msgType)) {
            Map eventMsgMap = JsonUtils.toObj(kafkaPushMsg.getMsg(), Map.class);
            if (eventMsgMap != null) {
                String applyId = StringUtils.obj2str(eventMsgMap.get("id"));
                if (eventMsgMap != null) {
                    // 钉钉审批记录考勤
                    OpenSysConfig OpenSysConfig = openSysConfigDao.getOpenSysConfigByTypeCode(OpenSysConfigType.DINGTALK_ATTENDANCE_APPROVE.getType(), companyId);
                    if (!ObjectUtils.isEmpty(OpenSysConfig)) {
                        attendanceApprove(applyId, kafkaPushMsg.getThirdEmployeeId(), corpId, MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri), kafkaPushMsg);
                    }
                }
            }
        }
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(msgType)) {
            sendTemplateMessage( request,  corpId);
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
        List<DingtalkApproveTask> dingtalkApproveTasks = pushMsgUpdateStatus(applyId, agentId, corpId);
        //3 修改历史数据状态
        dingtalkApproveTaskService.updateMessageStatus(dingtalkApproveTasks);
        // 4、表里新增数据
        DingtalkApproveTask dingtalkApprove = DingtalkApproveTask.builder().approveId(applyId).userId(userId).taskId(taskId).build();
        dingtalkApproveTaskService.insertDingtalkApproveTask(dingtalkApprove);
    }

    //推送历史消息修改状态
    public List<DingtalkApproveTask> pushMsgUpdateStatus(String applyId, long agentId, String corpId) {
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


    //原消息体(action_card)
    private OapiMessageCorpconversationAsyncsendV2Request setCardMsg(KafkaPushMsg kafkaPushMsg, String thirdEmployeeId, long agentId, String corpId) {
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
        //2:消费通知 4:审批通知 8:订单通知 32:系统通知
//        String msgType = kafkaPushMsg.getMsgType();
//        if (DingtalkIsvConstant.MSG_TYPE_ORDER.equals(msgType)) {
//            String url = initOrderUrl(kafkaPushMsg, corpId);
//            msg.getActionCard().setSingleUrl(url);
//        } else if (DingtalkIsvConstant.MSG_TYPE_APPLY.equals(msgType)) {
//            String url = initApplicationUrl(kafkaPushMsg, corpId);
//            msg.getActionCard().setSingleUrl(url);
//        }
//        if (StringUtils.isBlank(msg.getActionCard().getSingleUrl())) {
//            log.info("【push信息】未匹配到Url, 使用订单列表页面");
//            msg.getActionCard().setSingleUrl(webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER, corpId));
//        }

        String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_ISV_APP_HOME, corpId);
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        msg.getActionCard().setSingleUrl(messageUrl);
        request.setMsg(msg);
        return request;
    }

    /**
     * 初始化订单跳转的url
     *
     * @param kafkaPushMsg
     */
    private String initOrderUrl(KafkaPushMsg kafkaPushMsg, String corpId) {
        String msg = kafkaPushMsg.getMsg();
        Map map = JsonUtils.toObj(msg, Map.class);
        String url = "";
        if (map != null) {
            String orderType = StringUtils.obj2str(map.get("order_type"));
            String orderId = StringUtils.obj2str(map.get("order_id"));
            if (!StringUtils.isBlank(orderType) && !StringUtils.isBlank(orderId)) {
                int orderTypeInt = Integer.valueOf(orderType);
                OrderType ot = OrderType.getEnum(orderTypeInt);
                switch (ot) {
                    case Air:
                        url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER_AIR, corpId, orderId);
                        break;
                    case Hotel:
                        url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER_HOTEL, corpId, orderId);
                        break;
                    case Taxi:
                        url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER_TAXI, corpId, orderId);
                        break;
                    case Train:
                        url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER_TRAIN, corpId, orderId);
                        break;
                    default:
                        log.info("未匹配到场景类型");
                        break;
                }
            }
        }
        return url;
    }

    /**
     * 初始化审批跳转的url
     *
     * @param kafkaPushMsg
     */
    private String initApplicationUrl(KafkaPushMsg kafkaPushMsg, String corpId) {
        String msg = kafkaPushMsg.getMsg();
        Map map = JsonUtils.toObj(msg, Map.class);
        String url = "";
        if (map != null) {
            Integer applyType = (Integer) map.get("apply_type");
            String id = StringUtils.obj2str(map.get("id"));
            String settingType = StringUtils.obj2str(map.get("setting_type"));
            String viewType = StringUtils.obj2str(map.get("view_type"));
            //saas_push的view_type，1申请人，2审批人，3抄送人。 给前端跳转的type，1.审批人，2申请人，3抄送人
            if ("1".equals(viewType)) {
                viewType = "2";
            } else if ("2".equals(viewType)) {
                viewType = "1";
            }
            if ("1".equals(settingType)) {
                if (applyType != null && !StringUtils.isBlank(id)) {
                    if (applyType == 1) {
                        url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_APPLICATION_TRIP, corpId, id, viewType);
                    } else if (applyType == 12) {
                        url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_APPLICATION_TAXI, corpId, id, viewType);
                    }
                }
                //订单审批
            } else if ("2".equals(settingType)) {
                String orderType = StringUtils.obj2str(map.get("order_type"));
                if (orderType != null && !StringUtils.isBlank(id)) {
                    url = webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_APPLICATION_DETAIL, corpId, id, viewType);
                }
            }
        }
        return url;
    }

    /**
     * 发送模版消息
     * @param request
     * @param corpId
     * @return
     */
    public long sendTemplateMessage(OapiMessageCorpconversationSendbytemplateRequest request, String corpId) {
        String url = dingtalkHost + "topapi/message/corpconversation/sendbytemplate";
        OapiMessageCorpconversationSendbytemplateResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return response.getTaskId();
    }

    public long sendMessage(OapiMessageCorpconversationAsyncsendV2Request request, String corpId) {
        String url = dingtalkHost + "topapi/message/corpconversation/asyncsend_v2";
        OapiMessageCorpconversationAsyncsendV2Response response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return response.getTaskId();
    }

    public void updateStatusBar(OapiMessageCorpconversationStatusBarUpdateRequest request, String corpId) {
        String url = dingtalkHost + "topapi/message/corpconversation/status_bar/update";
        dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
    }

    public void test() {
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList("manager4801");
        request.setAgentId(835717249L);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
        msg.getActionCard().setTitle("待审核");
        msg.getActionCard().setMarkdown("【审批通知】小四提交了火车票退票申请，火车票在07月11日10:54后无法完成在线退票，请尽快完成审");
        msg.getActionCard().setSingleTitle("待审核");
        msg.getActionCard().setSingleUrl(webappHost + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER_AIR, "ding92efcd16aa085fedf5bf40eda33b7ba0", 1));
        msg.setMsgtype("action_card");
        request.setMsg(msg);
        sendMessage(request, "ding92efcd16aa085fedf5bf40eda33b7ba0");
    }

    public static void main(String[] args) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList("manager6709,015557354532558896");
        request.setAgentId(836731253L);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
        msg.getActionCard().setTitle("待审核");
        msg.getActionCard().setMarkdown("【审批通知】小四提交了火车票退票申请，火车票在07月11日10:54后无法完成在线退票，请尽快完成审");
        msg.getActionCard().setSingleTitle("待审核");
        msg.getActionCard().setSingleUrl("https://dd-webapp-dev.fenbeijinfu.com" + String.format(DingtalkIsvConstant.MESSAGE_URL_ORDER_AIR, "dingbb776b2ec14ac53bacaaa37764f94726", 1));
        msg.setMsgtype("action_card");
        request.setMsg(msg);
        String accessToken = "14e01383f57238d990fa9b469df1e110";
        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, accessToken);
        System.out.println(JsonUtils.toJson(request));
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
                String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
                switch (kafkaPushMsg.getTitle()) {
                    // 完成
                    case DingTalkConstant.custformApplyState.FINISH:
                        dingtalkAttendanceApprove.execute(companyId, applyId, userId, msgUrl, accessToken, dingtalkHost);
                        break;
                    case DingTalkConstant.custformApplyState.REVOCATION:
                        dingtalkAttendanceApprove.approveCancel(userId, applyId, accessToken, dingtalkHost);
                        break;
                }
            }
        }
    }

}
