package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserContactRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.eia.constant.ApplyCategoryEnum;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaUserAuthService;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaohai
 * @date 2022/08/25
 */
@Slf4j
@ServiceAspect
@Service
public class FeishuWorkrecordService {

    @Value("${host.webapp}")
    private String webappHost;

    @Value("${feishu.isv.appId}")
    private String appId;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private FeiShuEiaUserAuthService feiShuEiaUserAuthService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    public void pushWOrkrecodInfo( Map commonRecord ){
        if(!ObjectUtils.isEmpty(commonRecord)){
            WebHookOrderDTO webHookOrderDTO = commonRecord.get("webhookOrder")==null?null: JsonUtils.toObj(JsonUtils.toJson(commonRecord.get("webhookOrder")),WebHookOrderDTO.class);
            if(ObjectUtils.isEmpty(webHookOrderDTO)){
                log.info("推送数据：webhookOrder数据不能为空！！！" );
                return ;
            }
            ThirdCallbackRecord record = commonRecord.get("record")==null?null: JsonUtils.toObj(JsonUtils.toJson(commonRecord.get("record")),ThirdCallbackRecord.class);
            if(ObjectUtils.isEmpty(webHookOrderDTO)){
                log.info("推送数据：record数据不能为空！！！" );
                return ;
            }
            //查询企业类型
            OpenCompanySourceType openCompanySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(webHookOrderDTO.getCompanyId());
            if ( ObjectUtils.isEmpty(openCompanySourceType) ){
                log.info("未查询到企业类型：companyId:{}" , webHookOrderDTO.getCompanyId());
                return ;
            }
            String messageUrl = "";
            String uri = "";
            String thirdCompanyId = openCompanySourceType.getThirdCompanyId();
            Map<String, Object> kafkaPushMsg = buildMessageInfo(webHookOrderDTO);
            if( openCompanySourceType.getOpenType().equals(OpenType.FEISHU_EIA.getType())){
                messageUrl = eiaMessageUrl(kafkaPushMsg, openCompanySourceType.getThirdCompanyId());
                uri = webappHost + String.format(FeiShuConstant.FEISHU_EIA_APP_HOME, thirdCompanyId);
            }else if(openCompanySourceType.getOpenType().equals(OpenType.FEISHU_ISV.getType())){
                messageUrl = isvMessageUrl( kafkaPushMsg ,  appId );
                revertUserId( webHookOrderDTO );
            }
            //跳转地址链接
            webHookOrderDTO.setPcJumpLink(messageUrl);
            webHookOrderDTO.setAppJumpLink(messageUrl);
            //分贝券的发起链接
            if (ApplyCategoryEnum.OPEN_APPLY_TYPE_FENBEI_TICKET.getTypeId().equals(webHookOrderDTO.getApplyOrderType())) {
                //pc端发起链接
                webHookOrderDTO.setPcSponsorLink(uri.concat("&redirectFbtUrl=").concat("views/couponApply"));
            }
            if(ApplyCategoryEnum.OPEN_APPLY_TYPE_MILEAG.getTypeId().equals(webHookOrderDTO.getApplyOrderType())){
                webHookOrderDTO.setPcSponsorLink(webappHost.concat("/nav/normal/views/mileage"));
            }
            record.setCallbackType(CallbackType.APPLY_ORDER_REVERSE_PUSH.getType());
            record.setCallbackData(JsonUtils.toJson(webHookOrderDTO));
            recordDao.saveSelective(record);
            businessDataPushService.pushData(webHookOrderDTO.getCompanyId(), record, 0, 2);
        }

    }

    /**
     * 内嵌版消息跳转链接
     * @param kafkaPushMsg
     * @param corpId
     * @return
     */
    private String eiaMessageUrl(Map<String, Object> kafkaPushMsg ,String corpId){
        String uri = webappHost + String.format(FeiShuConstant.FEISHU_EIA_APP_HOME, corpId);
        String messageUrl = MessagePushUtils.initApplicationUrl(kafkaPushMsg, uri);
        messageUrl = messageUrl.replace("url=", "redirectFbtUrl=");
        return messageUrl;
    }


    /**
     * 市场版消息跳转链接
     * @param kafkaPushMsg
     * @param appId
     * @return
     */
    private String isvMessageUrl(Map<String, Object> kafkaPushMsg , String appId){
        String uri = String.format(FeiShuConstant.FEISHU_ISV_APPLINK_HOME_URL, appId );
        String messageUrl = MessagePushUtils.initApplicationUrl(kafkaPushMsg, "");
        try {
            if (!StringUtils.isBlank(messageUrl)) {
                messageUrl =  messageUrl.replace("?", "&");
                String loginUrl = "pages/login/index?";
                messageUrl = loginUrl + messageUrl.replace("url=", "redirectFbtUrl=");
                String encodeUrl = URLEncoder.encode(messageUrl, "utf-8");
                return uri + encodeUrl;
            }
        } catch (UnsupportedEncodingException e) {
            log.warn("url编码异常：messageUrl ： {}" , messageUrl );
            throw new OpenApiFeiShuException(FeiShuResponseCode.AES_ERROR , e.getMessage());
        }
        return "";
    }

    /**
     * 消息类型信息
     * @param webHookOrderDTO
     * @return
     */
    private Map<String, Object> buildMessageInfo(WebHookOrderDTO webHookOrderDTO){
        Map<String, Object> eventMsgMap = Maps.newHashMap();
        eventMsgMap.put("setting_type",webHookOrderDTO.getApplyOrderType());
        eventMsgMap.put("id",webHookOrderDTO.getApplyOrderId());
        eventMsgMap.put("apply_type",webHookOrderDTO.getApplyType());
        //1.审批人，2申请人，3抄送人
        eventMsgMap.put("view_type",webHookOrderDTO.getViewType());
        //订单类型（具体场景）
        eventMsgMap.put("order_type",webHookOrderDTO.getOrderType());
        return eventMsgMap;
    }


    /**
     *  待办消息接收的员工id只能是userId，市场版三方人员id对应的是openId，使用自建应用的信息换取到userid。
     * @param webHookOrderDTO
     */
    private void revertUserId(WebHookOrderDTO webHookOrderDTO){
        String companyId = webHookOrderDTO.getCompanyId();
        OpenMsgSetup companyWebhook = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, "company_webhook");
        if(companyWebhook == null  ||  StringUtils.isBlank( companyWebhook.getStrVal3() )){
            log.info("配置信息有误，请联系管理员配置！！！ " );
            throw new OpenApiFeiShuException(FeiShuResponseCode.CONFIGURATION_ERROR);
        }
        String appInfo = companyWebhook.getStrVal3();
        Map<String,String> map = JsonUtils.toObj(appInfo, Map.class);
        String appIdEia = map.get("app_id");
        String appSecret = map.get("app_secret");
        if( StringUtils.isBlank( appIdEia ) || StringUtils.isBlank( appSecret )){
            log.info("配置信息有误，请联系管理员配置！！！ " );
            throw new OpenApiFeiShuException(FeiShuResponseCode.CONFIGURATION_ERROR);
        }
        String approverPhone = webHookOrderDTO.getApproverPhone();
        String starterPhone = webHookOrderDTO.getStarterPhone();
        String notifierPhone = webHookOrderDTO.getNotifierPhone();
        if(!StringUtils.isBlank(approverPhone)){
            List<String> phones = new ArrayList<>();
            phones.add(approverPhone);
            webHookOrderDTO.setApproverId( getFeishuUserid(appIdEia, appSecret, phones) );
        }
        if(!StringUtils.isBlank(starterPhone)){
            List<String> phones = new ArrayList<>();
            phones.add(starterPhone);
            webHookOrderDTO.setStarterId( getFeishuUserid(appIdEia, appSecret, phones) );
        }
        if(!StringUtils.isBlank(notifierPhone)){
            List<String> phones = new ArrayList<>();
            phones.add(notifierPhone);
            webHookOrderDTO.setNotifierId( getFeishuUserid(appIdEia, appSecret, phones) );
        }

    }

    private String getFeishuUserid(String appId  , String appSecret ,List<String> phones){
        FeiShuUserContactRespDTO feiShuUserContactRespDTO = feiShuEiaUserAuthService.userSingleDetailGet(appId, appSecret, phones);
        if (feiShuUserContactRespDTO == null || 0 != feiShuUserContactRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_SINGLE_USER_INFO_FAILED);
        }
        return feiShuUserContactRespDTO.getData().getUserList().get(0).getUserId();
    }

}
