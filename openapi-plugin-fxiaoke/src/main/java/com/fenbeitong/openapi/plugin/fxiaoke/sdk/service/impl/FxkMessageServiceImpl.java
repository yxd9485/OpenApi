package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.constant.FxkConstant;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.util.FxkHttpUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeGetByNickNameRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeSendMessageReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkCorpAppService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkMessageService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkSyncService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkUserAuthService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCompanyClientType;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2020/3/28.
 */
@ServiceAspect
@Service
@Slf4j
public class FxkMessageServiceImpl implements IFxkMessageService {


    @Value("${host.webapp}")
    private String webappHost;

    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private IFxkCorpAppService fxkCorpAppService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private IFxkSyncService fxkSyncService;

    @Autowired
    private FxkHttpUtils fxkHttpUtils;

    @Autowired
    private IFxkUserAuthService fxkUserAuthService;

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    /**
     * 向企业微信推送消息
     *
     * @return
     */
    @Override
    public FxiaokeRespDTO pushMessage(WebAppPushEvents kafkaPushMsg) {
        String companyId = kafkaPushMsg.getReceiveCompanyId();
        String userId = kafkaPushMsg.getUserId();
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        String employeeNumber = kafkaPushMsg.getEmployeeNumber();
        String thirdEmployeeId = kafkaPushMsg.getThirdEmployeeId();
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
            log.info("【push信息】非Fxiaoke企业,companyId:{}", companyId);
            return null;
        }
        String corpId = pluginCorpDefinition.getThirdCorpId();
        FxiaokeCorpApp fxkCorpApp = fxkCorpAppService.getFxkCorpAppByCorpId(corpId);
        if (fxkCorpApp == null) {
            log.info("【push信息】非Fxiaoke企业,companyId:{}", companyId);
            return null;
        }
        boolean b = fxkUserAuthService.useNickNameToEmployeeNumber(companyId);
        if (b) {
            FxiaokeGetByNickNameRespDTO userByNickName = fxkSyncService.getUserByNickName(employeeNumber, corpId);
            List<FxiaokeGetByNickNameRespDTO.Employee> empList = userByNickName.getEmpList();
            if (ObjectUtils.isEmpty(empList)) {
                log.info("【push信息】用户不存在, 别名从Fxiaoke查询到人员,companyId:{},userId:{}", companyId, userId);
                return null;
            }
            FxiaokeGetByNickNameRespDTO.Employee emp = empList.get(0);
            thirdEmployeeId = emp.getOpenUserId();
        }
        FxiaokeSendMessageReqDTO fxiaokeSendMessageReqDTO = new FxiaokeSendMessageReqDTO();
        fxiaokeSendMessageReqDTO.setCorpId(corpId);
        fxiaokeSendMessageReqDTO.setToUser(Lists.newArrayList(thirdEmployeeId));
        fxiaokeSendMessageReqDTO.setMsgType("composite");
        FxiaokeSendMessageReqDTO.Composite composite = new FxiaokeSendMessageReqDTO.Composite();
        fxiaokeSendMessageReqDTO.setComposite(composite);
        FxiaokeSendMessageReqDTO.Head head = new FxiaokeSendMessageReqDTO.Head();
        FxiaokeSendMessageReqDTO.First first = new FxiaokeSendMessageReqDTO.First();
        FxiaokeSendMessageReqDTO.Link link = new FxiaokeSendMessageReqDTO.Link();
        composite.setHead(head);
        composite.setFirst(first);
        composite.setLink(link);
        head.setTitle(title);
        first.setContent(content);
        String uri = webappHost + String.format(FxkConstant.webapp.EIA_WEB_APP_HOME, fxkCorpApp.getAppId());
        String messageUrl = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, uri);
        link.setTitle("详情");
        link.setUrl(messageUrl);
        return sendMessage(fxiaokeSendMessageReqDTO, corpId);
    }


    public FxiaokeRespDTO sendMessage(FxiaokeSendMessageReqDTO message, String corpId) {
        String url = fxiaokeHost.concat("/cgi/message/send");
        Map<String, Object>  data = MapUtils.obj2map(message, false);
        String res = fxkHttpUtils.postJsonWithAccessToken(url, data, corpId);
        FxiaokeRespDTO fxiaokeRespDTO = JsonUtils.toObj(res, FxiaokeRespDTO.class);
        return fxiaokeRespDTO;
    }

}
