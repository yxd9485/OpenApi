package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.message;

import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
import com.fenbeitong.openapi.plugin.support.apply.dto.CompanyApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.SendMessageDto;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.webhook.constant.ApplyTypeEnum;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaUrlConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaMsgSendDao;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaMsgSend;
import com.fenbeitong.openapi.plugin.yunzhijia.notice.sender.YunzhijiaNoticeSender;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaEmployeeService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaMessageService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaPublicNoticeService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply.YunzhijiaApplyServiceImpl;
import com.fenbeitong.openapi.plugin.yunzhijia.utils.PublicPubTokenUtil;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 向云之家推送消息
 * @Auther zhang.peng
 * @Date 2021/7/28
 */
@Slf4j
@ServiceAspect
@Service
public class YunzhijiaMessageServiceImpl implements IYunzhijiaMessageService {

    @Autowired
    private YunzhijiaNoticeSender yunzhijiaNoticeSender;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private IYunzhijiaEmployeeService yunzhijiaEmployeeService;

    @Autowired
    private IYunzhijiaPublicNoticeService publicNoticeService;

    @Autowired
    private YunzhijiaMsgSendDao yunzhijiaMsgSendDao;

    @Autowired
    private PluginCorpAppDefinitionDao corpAppDefinitionDao;

    @Value("${host.webapp}")
    private String webappHost;

    @Autowired
    private YunzhijiaApplyServiceImpl yunzhijiaApplyServiceImpl;

    @Autowired
    private UserCenterService userCenterService;

    @Override
    public void pushMessageByPublicModel(SaasPushEvents kafkaPushMsg) {
        log.info("推送云之家消息 start ");
        log.info("云之家接收到消息,内部消息的消息体为：{}", kafkaPushMsg);
        String companyId = kafkaPushMsg.getReceiveCompanyId();
        String userId = kafkaPushMsg.getUserId();
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        String msg = kafkaPushMsg.getMsg();
        String thirdEmployeeId = kafkaPushMsg.getThirdEmployeeId();
        String applyId = StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(msg, Map.class),"id"));
        int applyType = NumberUtils.toInt(StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(msg, Map.class),"apply_type")));
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
            return;
        }
        //查询企业授权信息
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if (pluginCorpDefinition == null) {
            log.info("【push信息】非云之家企业,companyId:{}", companyId);
            return;
        }
        String corpId = pluginCorpDefinition.getThirdCorpId();
        PluginCorpAppDefinition pluginCorpAppDefinition = corpAppDefinitionDao.getByCorpId(corpId);
        List<String> userIds = new ArrayList<>();
        userIds.add(thirdEmployeeId);
        String message=msgTitle(companyId,applyId,applyType);
        String appId = "";
        if ( null != pluginCorpAppDefinition ){
            appId = pluginCorpAppDefinition.getThirdAgentId() + "";
        }
        String urlParam = "";
        urlParam = MessagePushUtils.messageUrlTransfer(kafkaPushMsg, urlParam);
        if ( urlParam.contains("url") ){
            urlParam = urlParam.substring(urlParam.indexOf("=")+1);
        }
        try {
            urlParam = URLEncoder.encode(urlParam, "UTF-8");
        } catch (Exception e){
            log.warn("url encode error : {}",e.getMessage());
        }
        String messageUrl = webappHost + String.format(YunzhijiaUrlConstant.YUNZHIJIA_EIA_APP_HOME, corpId,appId, urlParam);
        log.info("message url : {}",messageUrl);
        SendMessageDto messageDto = SendMessageDto.builder().text(message)
                .url(messageUrl)
                .appid(appId)
                .todo(0)
                .build();
        YunzhijiaMsgSend msgSend = yunzhijiaMsgSendDao.getMsgSendByCorpId(corpId);
        boolean isOpen = publicNoticeService.isSubscribe(msgSend.getAgentId(),corpId);
        log.info("查询是否订阅公共号结果 : {}",isOpen);
        if (!isOpen){
            long time =  System.currentTimeMillis()/1000;
            String token = PublicPubTokenUtil.getPubToken(corpId,msgSend.getAgentId(),msgSend.getAgentSecret(),false,time);
            log.info("未订阅公共号, 发起订阅, token : {} ",token);
            // ssb = 1 代表订阅
            boolean result = publicNoticeService.companySubscribe(msgSend.getAgentId(),corpId,"1",token,time);
            log.info("订阅结果 : {} ",result);
        }
        yunzhijiaNoticeSender.sender(corpId,userIds,messageDto);
        log.info("推送云之家消息 end");
    }
    /**
     * @author helu
     * @date 2022/2/24 下午4:45
     * 云之家审批类消息模版标题
     * @return 消息抬头
     */
    private  String msgTitle(String companyId,String applyId,int applyType){
        //调用审批详情接口查询申请人名称
        String token = userCenterService.getUcSuperAdminToken(companyId);
        CompanyApplyDetailReqDTO companyApplyDetailReqDTO = CompanyApplyDetailReqDTO.builder().applyId(applyId).build();
        Map<String, Object> companyApproveDetail = yunzhijiaApplyServiceImpl.getCompanyApproveDetail(token, companyApplyDetailReqDTO);
        if (MapUtils.isBlank(companyApproveDetail)){
            log.info("云之家用车审批消息查询审批详情失败");
            throw new OpenApiPluginSupportException(SupportRespCode.APPROVE_DETAIL_DATA_FAILED);
        }
        String applicantName = StringUtils.obj2str(MapUtils.getValueByExpress(companyApproveDetail, "apply:applicant_name"));
        if(StringUtils.isBlank(applicantName)){
            log.info("公司：{},审批单id:{},申请人姓名为空",companyId,applyId);
        }
        StringBuilder msg = new StringBuilder();
        msg.append("您有一条");
        msg.append(applicantName);
        msg.append("发起的");
        msg.append(ApplyTypeEnum.getTypeNameById(applyType));
        msg.append("审批单待审批");
        return msg.toString();
    }
}
