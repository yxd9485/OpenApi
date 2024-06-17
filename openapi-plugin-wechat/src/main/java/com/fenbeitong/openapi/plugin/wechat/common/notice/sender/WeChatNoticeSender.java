package com.fenbeitong.openapi.plugin.wechat.common.notice.sender;

import com.fenbeitong.common.utils.basis.BooleanUtils;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.AbstractNoticeSender;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.dao.MsgRecipientDefinitionDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCompanyClientType;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatUserIdGetRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatEiaConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.PluginCallWeChatEiaService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvSendMessageRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvSendMessageResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by dave.hansins on 19/12/8.
 */
@Component
@Slf4j
public class WeChatNoticeSender extends AbstractNoticeSender {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Autowired
    PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    MsgRecipientDefinitionDao msgRecipientDefinitionDao;
    @Autowired
    PluginCallWeChatEiaService pluginCallWeChatEiaService;
    @Autowired
    private RestHttpUtils httpUtil;

    @Autowired
    private WeChatEmployeeService weChatEmployeeService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenEmployeeExtServiceImpl employeeExtService;

    private static  final String WECHAT_USERID_CONVERT="wechat_userId_convert";

    @Override
    public void sender(String companyId, String userId, String msg) {
        //置换corpId
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if (!ObjectUtils.isEmpty(pluginCorpDefinition)) {
            String corpId = pluginCorpDefinition.getThirdCorpId();
            //根据corpId查询应用信息
            PluginCorpAppDefinition AppPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
            if (!ObjectUtils.isEmpty(AppPluginCorpApp)) {
                //针对需要根据手机号推送消息通知的客户进行定制化配置，查询open_msg_setup表是否有配置三方人员id转换
                OpenMsgSetup userIdConvertSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, WECHAT_USERID_CONVERT);
                if(!ObjectUtils.isEmpty(userIdConvertSetup)){
                    //根据三方人员id查询人员手机号
                    UcEmployeeDetailDTO ucEmployeeDetailInfo = employeeExtService.loadUserData(companyId,userId);
                    if(ObjectUtils.isEmpty(ucEmployeeDetailInfo) || ObjectUtils.isEmpty(ucEmployeeDetailInfo.getEmployee())){
                        log.info("企微查询三方人员信息失败，公司id:{},三方人员id:{}",companyId,userId);
                        throw  new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_SEND_MESSAGE_FAILED));
                    }
                    String phoneNum = ucEmployeeDetailInfo.getEmployee().getPhone_num();
                    //调用企微根据手机号获取该人员的三方id
                    String appAccessToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
                    WeChatUserIdGetRespDTO weChatUserId = pluginCallWeChatEiaService.getWeChatUserId(appAccessToken, phoneNum);
                    if(ObjectUtils.isEmpty(weChatUserId) || StringUtils.isTrimBlank(weChatUserId.getUserid())){
                        log.info("根据人员手机号查询企微三方人员id失败，corpId:{},userId:{}",corpId,userId);
                        throw  new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_SEND_MESSAGE_FAILED));
                    }
                    userId=weChatUserId.getUserid();
                }

                //企业微信消息通知应用
                Long agentId = AppPluginCorpApp.getThirdAgentId();
                String appAccessToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
                String url = wechatHost + "/cgi-bin/message/send?access_token=" + appAccessToken;
                HashMap<String, Object> msgRecipientMap = Maps.newHashMap();
                HashMap<String, String> msgContentMap = Maps.newHashMap();
                msgContentMap.put("content", msg);
                msgRecipientMap.put("touser", userId);
                msgRecipientMap.put("msgtype", "text");
                msgRecipientMap.put("agentid", agentId);
                msgRecipientMap.put("text", msgContentMap);
                msgRecipientMap.put("safe", 0);

                log.info("发送企业微信消息通知对象 {}", JsonUtils.toJson(msgRecipientMap));
                String msgResult = httpUtil.postJson(url, JsonUtils.toJson(msgRecipientMap));

                log.info("发送消息通知返回结果 {}", msgResult);
                return;
            }
        }
        log.info("企业未注册");
    }


    /**
     * 向企业微信推送卡片消息
     *
     * @return
     */
    public WeChatIsvSendMessageResponse pushMessage(WebAppPushEvents kafkaPushMsg) {
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(kafkaPushMsg.getMsgType())) {
            //企业ID，如果配置了集团就取集团的主企业ID，以支持集团版，注意查人员等原始数据时的转换
            String companyId = kafkaPushMsg.getReceiveCompanyId();
            String userId = kafkaPushMsg.getUserId();
            String title = kafkaPushMsg.getTitle();
            String content = kafkaPushMsg.getContent();
            if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
                log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
                return null;
            }
            OpenCompanySourceType openCompanySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(companyId);
            Integer clientType = openCompanySourceType.getClientType();
            if (!OpenCompanyClientType.H5.getType().equals(clientType)) {
                log.info("【push信息】消息推送失败，client_type不正确{}", JsonUtils.toJson(kafkaPushMsg));
                return null;
            }
            //查询企业授权信息
            PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
            if (pluginCorpDefinition == null) {
                log.info("【push信息】非企业微信企业,companyId:{}", companyId);
                return null;
            }
            PluginCorpAppDefinition pluginCorpAppDefinition = pluginCorpAppDefinitionDao.getByCorpId(pluginCorpDefinition.getThirdCorpId());
            if (!ObjectUtils.isEmpty(pluginCorpAppDefinition)) {
                //针对需要根据手机号推送消息通知的客户进行定制化配置，查询open_msg_setup表是否有配置三方人员id转换
                OpenMsgSetup userIdConvertSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, WECHAT_USERID_CONVERT);
                if (!ObjectUtils.isEmpty(userIdConvertSetup)) {
                    String phoneNum = kafkaPushMsg.getPhoneNum();
                    //调用企微根据手机号获取该人员的三方id
                    String appAccessToken = wechatTokenService.getWeChatAppTokenByCorpId(pluginCorpAppDefinition.getThirdCorpId());
                    WeChatUserIdGetRespDTO weChatUserId = pluginCallWeChatEiaService.getWeChatUserId(appAccessToken, phoneNum);
                    if (ObjectUtils.isEmpty(weChatUserId) || StringUtils.isTrimBlank(weChatUserId.getUserid())) {
                        log.info("根据人员手机号查询企微三方人员id失败，corpId:{},userId:{}", pluginCorpAppDefinition.getThirdCorpId(), userId);
                        throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_SEND_MESSAGE_FAILED));
                    }
                    kafkaPushMsg.setThirdEmployeeId(weChatUserId.getUserid());
                }
                // 通过获取敏感信息（手机号）进行免登标识
                boolean sensitiveFlag = isSensitiveLogin(userIdConvertSetup);
                Long agentId = pluginCorpAppDefinition.getThirdAgentId();
                String corpId = pluginCorpDefinition.getThirdCorpId();
                WeChatIsvSendMessageRequest weChatIsvSendMessageRequest = new WeChatIsvSendMessageRequest();
                weChatIsvSendMessageRequest.setTouser(kafkaPushMsg.getThirdEmployeeId());
                weChatIsvSendMessageRequest.setAgentId(NumericUtils.obj2int(agentId));
                weChatIsvSendMessageRequest.setMsgType("textcard");
                initTextCardMsg(weChatIsvSendMessageRequest, kafkaPushMsg, corpId, sensitiveFlag);
                boolean isUnion = false;
                if (!StringUtils.isBlank(kafkaPushMsg.getThirdEmployeeId()) &&kafkaPushMsg.getThirdEmployeeId().contains("/")) {
                    isUnion = true;
                }
                return sendMessage(weChatIsvSendMessageRequest, corpId, isUnion);
            }
        }
        return null;
    }

    /**
     * 如果有通过手机号推送消息的开关，且开关中有参数sensitiveFlag为true
     * 则企业是通过获取敏感信息（手机号）进行免登的
     * 免登Url需要添加agentId以及sensitiveFlag参数
     *
     * @param userIdConvertSetup 手机号免登开关
     */
    private boolean isSensitiveLogin(OpenMsgSetup userIdConvertSetup) {
        if (ObjectUtils.isEmpty(userIdConvertSetup) || StringUtils.isBlank(userIdConvertSetup.getStrVal1())) {
            return false;
        }
        Map str1Map = JsonUtils.toObj(userIdConvertSetup.getStrVal1(), HashMap.class);
        if (str1Map == null || str1Map.get("sensitiveFlag") == null) {
            return false;
        }
        return BooleanUtils.obj2bool(str1Map.get("sensitiveFlag"));
    }

    /**
     * 初始化textcard消息
     *
     * @param weChatIsvSendMessageRequest 微信消息推送请求体
     * @param kafkaPushMsg                kafka 推送消息体
     * @param corpId                      三方公司id
     * @param sensitiveFlag               通过敏感信息（手机号）进行免登标识
     */
    private void initTextCardMsg(WeChatIsvSendMessageRequest weChatIsvSendMessageRequest, KafkaPushMsg kafkaPushMsg, String corpId, boolean sensitiveFlag) {
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        content = "<div class=\"gray\">" + DateUtils.toSimpleStr(DateUtils.now()) + "</div> <div class=\"normal\">" + content + "</div>";
        String btntxt = "查看详情";

        WeChatIsvSendMessageRequest.Textcard textcard = new WeChatIsvSendMessageRequest.Textcard();
        textcard.setTitle(title);
        textcard.setDescription(content);
        textcard.setBtntxt(btntxt);
        weChatIsvSendMessageRequest.setTextCard(textcard);
        String uri;
        if (sensitiveFlag) {
            uri = webappHost + String.format(WeChatEiaConstant.WECHAT_EIA_SENSITIVE_APP_HOME, corpId, weChatIsvSendMessageRequest.getAgentId(), true);
        } else {
            uri = webappHost + String.format(WeChatEiaConstant.WECHAT_EIA_APP_HOME, corpId);
        }
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        textcard.setUrl(messageUrl);
    }


    /**
     * 向微信推消息
     *
     * @param weChatIsvSendMessageRequest
     * @param corpId
     * @param isUnion                     是否是互联企业
     * @return
     */
    public WeChatIsvSendMessageResponse sendMessage(WeChatIsvSendMessageRequest weChatIsvSendMessageRequest, String corpId, boolean isUnion) {
        String accessToken = wechatTokenService.getWeChatAppTokenByCorpId(corpId);
        String url = "";
        if (isUnion) {
            url = wechatHost + "/cgi-bin/linkedcorp/message/send?access_token=" + accessToken;
        } else {
            url = wechatHost + "/cgi-bin/message/send?access_token=" + accessToken;
        }
        String res = httpUtil.postJson(url, JsonUtils.toJson(weChatIsvSendMessageRequest));
        WeChatIsvSendMessageResponse weChatIsvSendMessageResponse = (WeChatIsvSendMessageResponse) JsonUtils.toObj(res, WeChatIsvSendMessageResponse.class);
        if (weChatIsvSendMessageResponse == null || Optional.ofNullable(weChatIsvSendMessageResponse.getErrcode()).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_SEND_MESSAGE_FAILED));
        }
        return weChatIsvSendMessageResponse;
    }
}
