package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvBaseRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvSendMessageReqDTO;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.util.WeLinkIsvHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * Created by lizhen on 2020/4/20.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvMessageService {

    @Value("${welink.api-host}")
    private String welinkHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Autowired
    private WeLinkIsvHttpUtils weLinkIsvHttpUtils;

    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;

    @Autowired
    private WeLinkIsvEmployeeService weLinkIsvEmployeeService;
    /**
     * 向企业微信推送消息
     *
     * @return
     */
    public WeLinkIsvBaseRespDTO pushMessage(KafkaPushMsg kafkaPushMsg) {
        String companyId = kafkaPushMsg.getCompanyId();
        String userId = kafkaPushMsg.getUserId();
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
            return null;
        }
        //查询企业授权信息
        WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCompanyId(companyId);
        if (weLinkIsvCompanyTrial == null) {
            log.info("【push信息】非welink isv企业,companyId:{}", companyId);
            return null;
        }
        //查询分贝通信息
        EmployeeContract employee = weLinkIsvEmployeeService.getEmployeeByEmployeeId(companyId, userId);
        if (employee == null) {
            log.info("【push信息】用户不存在,companyId:{},userId:{}", companyId, userId);
            return null;
        }
        String corpId = weLinkIsvCompanyTrial.getCorpId();
        WeLinkIsvSendMessageReqDTO weLinkIsvSendMessageReqDTO = new WeLinkIsvSendMessageReqDTO();
        weLinkIsvSendMessageReqDTO.setMsgContent(kafkaPushMsg.getContent());
        weLinkIsvSendMessageReqDTO.setMsgOwner("分贝通");
        weLinkIsvSendMessageReqDTO.setMsgRange(0);
        weLinkIsvSendMessageReqDTO.setMsgTitle(kafkaPushMsg.getTitle());
        weLinkIsvSendMessageReqDTO.setToUserList(Lists.newArrayList(employee.getThird_employee_id()));
        weLinkIsvSendMessageReqDTO.setUrlPath("html");
        weLinkIsvSendMessageReqDTO.setUrlType("html");
        String uri = webappHost + WeLinkIsvConstant.WELINK_ISV_APP_HOME;
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        String msgType = kafkaPushMsg.getMsgType();//2:消费通知 4:审批通知 8:订单通知 32:系统通知
        if (WeLinkIsvConstant.MSG_TYPE_ORDER.equals(msgType)) {
            weLinkIsvSendMessageReqDTO.setUrlPath(messageUrl);
            weLinkIsvSendMessageReqDTO.setMsgOwner("订单");
        } else if (WeLinkIsvConstant.MSG_TYPE_APPLY.equals(msgType)) {
            weLinkIsvSendMessageReqDTO.setUrlPath(messageUrl);
            weLinkIsvSendMessageReqDTO.setMsgOwner("审批");
        }

        return sendMessage(weLinkIsvSendMessageReqDTO, corpId);
    }


    /**
     * 初始化订单跳转的url
     *
     * @param kafkaPushMsg
     */
    private String initOrderUrl(KafkaPushMsg kafkaPushMsg) {
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
                        url = webappHost + WeLinkIsvConstant.MESSAGE_URL_ORDER_AIR + orderId;
                        break;
                    case Hotel:
                        url = webappHost + WeLinkIsvConstant.MESSAGE_URL_ORDER_HOTEL + orderId;
                        break;
                    case Taxi:
                        url = webappHost + WeLinkIsvConstant.MESSAGE_URL_ORDER_TAXI + orderId;
                        break;
                    case Train:
                        url = webappHost + WeLinkIsvConstant.MESSAGE_URL_ORDER_TRAIN + orderId;
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
    private String initApplicationUrl(KafkaPushMsg kafkaPushMsg) {
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
            if("1".equals(settingType)) {
                if(applyType != null && !StringUtils.isBlank(id)) {
                    if(applyType == 1) {
                        url = webappHost + WeLinkIsvConstant.MESSAGE_URL_APPLICATION_TRIP + id + "&type=" + viewType;
                    } else if(applyType == 12) {
                        url = webappHost + WeLinkIsvConstant.MESSAGE_URL_APPLICATION_TAXI + id + "&type=" + viewType;
                    }
                }
                //订单审批
            } else if("2".equals(settingType)){
                String orderType = StringUtils.obj2str(map.get("order_type"));
                if (orderType != null && !StringUtils.isBlank(id)) {
                    url = webappHost + WeLinkIsvConstant.MESSAGE_URL_APPLICATION_DETAIL + id + "&type=" + viewType;
                }
            }
        }
        return url;
    }


    public WeLinkIsvBaseRespDTO sendMessage(WeLinkIsvSendMessageReqDTO weLinkIsvSendMessageReqDTO, String corpId) {
        String url = welinkHost + WeLinkIsvConstant.SEND_MESSAGE_URL;
        String res = weLinkIsvHttpUtils.postJsonWithAccessToken(url, JsonUtils.toJson(weLinkIsvSendMessageReqDTO), corpId);
        WeLinkIsvBaseRespDTO weLinkIsvBaseRespDTO = JsonUtils.toObj(res, WeLinkIsvBaseRespDTO.class);
        if (weLinkIsvBaseRespDTO == null || !"0".equals(weLinkIsvBaseRespDTO.getCode())) {
            log.info("welink isv sendMessage:{}", res);
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_ISV_SEND_MESSAGE_FAILED);
        }
        return weLinkIsvBaseRespDTO;
    }

}
