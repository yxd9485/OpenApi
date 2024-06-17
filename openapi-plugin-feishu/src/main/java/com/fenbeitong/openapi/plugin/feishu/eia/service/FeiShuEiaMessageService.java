package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserDetailRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvSendMessageReqDTO;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.CommonPluginCorpAppDefinitionService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCompanyClientType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/6/8
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaMessageService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private CommonPluginCorpAppDefinitionService commonPluginCorpAppDefinitionService;

    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    @Autowired
    private FeiShuEiaUserAuthService feiShuEiaUserAuthService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    /**
     * 向飞书推送消息
     *
     * @return
     */
    public void pushMessage(WebAppPushEvents kafkaPushMsg) {
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(kafkaPushMsg.getMsgType())) {
            String companyId = kafkaPushMsg.getReceiveCompanyId();
            String userId = kafkaPushMsg.getUserId();
            String title = kafkaPushMsg.getTitle();
            String content = kafkaPushMsg.getContent();
            if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
                log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
                return;
            }
            //查询企业授权信息
            PluginCorpDefinition pluginCorp = commonPluginCorpAppDefinitionService.getPluginCorpByCompanyId(companyId);
            if (pluginCorp == null) {
                log.info("【push信息】非飞书 eia企业,companyId:{}", companyId);
                return;
            }
            OpenCompanySourceType openCompanySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(companyId);
            Integer clientType = openCompanySourceType.getClientType();
            if (!OpenCompanyClientType.H5.getType().equals(clientType)) {
                log.info("【push信息】消息推送失败，client_type不正确{}", JsonUtils.toJson(kafkaPushMsg));
                return;
            }
            String corpId = pluginCorp.getThirdCorpId();
            FeiShuIsvSendMessageReqDTO feiShuIsvSendMessageReqDTO = new FeiShuIsvSendMessageReqDTO();
            FeiShuIsvSendMessageReqDTO.MsgContent msgContent = new FeiShuIsvSendMessageReqDTO.MsgContent();
            FeiShuIsvSendMessageReqDTO.Post post = new FeiShuIsvSendMessageReqDTO.Post();
            FeiShuIsvSendMessageReqDTO.ZhCn zhch = new FeiShuIsvSendMessageReqDTO.ZhCn();
            zhch.setTitle(title);
            FeiShuIsvSendMessageReqDTO.PostContent postContent1 = new FeiShuIsvSendMessageReqDTO.PostContent();
            // 第一行，content
            postContent1.setTag("text");
            postContent1.setUnEscape(true);
            postContent1.setText(content);
            FeiShuIsvSendMessageReqDTO.PostContent postContent2 = new FeiShuIsvSendMessageReqDTO.PostContent();
            // 第二行，超链接
            postContent2.setText("查看详情");
            postContent2.setTag("a");
            postContent1.setUnEscape(true);
            List contentList1 = Lists.newArrayList(postContent1);
            List contentList2 = Lists.newArrayList(postContent2);
            zhch.setContent(Lists.newArrayList(contentList1, contentList2));
            post.setZhCn(zhch);
            msgContent.setPost(post);
            feiShuIsvSendMessageReqDTO.setUserId(kafkaPushMsg.getThirdEmployeeId());
            feiShuIsvSendMessageReqDTO.setMsgType("post");
            feiShuIsvSendMessageReqDTO.setContent(msgContent);
            String uri = webappHost + String.format(FeiShuConstant.FEISHU_EIA_APP_HOME, corpId);
            String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, "");
            messageUrl = messageUrl.replace("url=", "redirectFbtUrl=");
            postContent2.setHref(uri + messageUrl);
            convertUserId(companyId, kafkaPushMsg.getPhoneNum(), corpId, feiShuIsvSendMessageReqDTO);
            sendMessage(feiShuIsvSendMessageReqDTO, corpId);
        }
    }

    private void convertUserId(String companyId, String phoneNum, String corpId,
        FeiShuIsvSendMessageReqDTO feiShuIsvSendMessageReqDTO) {
        try {
            OpenThirdScriptConfig messageConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.MESSAGE_SYNC);
            if (null == messageConfig) {
                log.info("未配置脚本,不需要处理");
                return;
            }
            log.info("配置消息脚本,开始转换三方id");
            FeiShuUserDetailRespDTO feiShuUserDetailRespDTO = feiShuEiaUserAuthService.userBatchGetDetailByPhoneOrEmail(phoneNum, "", corpId);
            if (null == feiShuUserDetailRespDTO) {
                log.info("feiShuUserDetailRespDTO is null , return");
                return;
            }
            log.info("飞书用户详情:{}", JsonUtils.toJson(feiShuUserDetailRespDTO));
            FeiShuUserDetailRespDTO.UserDetailResp userDetailResp = feiShuUserDetailRespDTO.getData();
            if (null == userDetailResp) {
                log.info("用户信息为空,return");
                return;
            }
            Map<String, List<FeiShuUserDetailRespDTO.UserInfo>> mobileInfo = userDetailResp.getMobileUsers();
            if (null == mobileInfo) {
                log.info("手机号信息为空,return");
                return;
            }
            List<FeiShuUserDetailRespDTO.UserInfo> userInfoList = mobileInfo.get(phoneNum);
            if (CollectionUtils.isBlank(userInfoList)) {
                log.info("手机号的用户集合信息为空,return");
                return;
            }
            FeiShuUserDetailRespDTO.UserInfo userInfo = userInfoList.get(0);
            if (null == userInfo) {
                log.info("飞书用户信息为空,return");
                return;
            }
            String feishuUserId = userInfo.getUserId();
            log.info("飞书详情用户id : {}", feishuUserId);
            feiShuIsvSendMessageReqDTO.setUserId(feishuUserId);
        } catch (Exception e) {
            log.info("飞书用户详情处理失败:{}", e.getMessage());
        }
    }


    public void sendMessage(FeiShuIsvSendMessageReqDTO feiShuIsvSendMessageReqDTO, String corpId) {
        sendMessage(JsonUtils.toJson(feiShuIsvSendMessageReqDTO), corpId);
    }

    public void sendMessage(String message, String corpId) {
        String url = feishuHost + FeiShuConstant.SEND_MESSAGE_URL;
        String res = feiShuEiaHttpUtils.postJsonWithTenantAccessToken(url, message, corpId);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (jsonObject == null || 0 != jsonObject.getInteger("code")) {
            log.warn("feishu eia sendMessage:{}", res);
            String msg = jsonObject == null ? "" : jsonObject.getString("msg");
            throw new OpenApiFeiShuException(FeiShuResponseCode.SEND_MESSAGE_FAILED, msg);
        }
    }

}
