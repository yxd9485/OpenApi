package com.fenbeitong.openapi.plugin.lanxin.eia.service.impl;

import com.fenbeitong.finhub.kafka.msg.saas.KafkaPushMsg;
import com.fenbeitong.openapi.plugin.core.constant.EventConstant;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebAppPushEvents;
import com.fenbeitong.openapi.plugin.lanxin.common.constant.LanXinConstant;
import com.fenbeitong.openapi.plugin.lanxin.common.dao.LanxinCorpDao;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.request.LanXinMsgDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.response.LanXinBaseDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.response.LanXinUserStaffIdDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.entity.LanxinCorp;
import com.fenbeitong.openapi.plugin.lanxin.common.service.LanXinService;
import com.fenbeitong.openapi.plugin.lanxin.eia.service.LanXInEiaMessageService;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCompanyClientType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * <p>Title: LanXinAuthServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 4:27 下午
 */
@ServiceAspect
@Service
@Slf4j
public class LanXinEiaMessageServiceImpl extends AbstractEmployeeService implements LanXInEiaMessageService {

    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;
    @Autowired
    private LanXinService lanXinService;
    @Autowired
    LanxinCorpDao lanxinCorpDao;
    @Value("${host.webapp}")
    private String webappHost;


    /**
     * 向蓝信推送消息
     *
     * @return
     */
    @SneakyThrows
    @Override
    public void pushMessage(WebAppPushEvents kafkaPushMsg) {
        String msgType = kafkaPushMsg.getMsgType();
        if (!EventConstant.MSG_TYPE_APPLY_CANCEL.equals(msgType)) {
            log.info("eia向蓝信推送消息内部消息的消息体为：{}", kafkaPushMsg);
            String companyId = kafkaPushMsg.getReceiveCompanyId();
            String userId = kafkaPushMsg.getUserId();
            String title = kafkaPushMsg.getTitle();
            String content = kafkaPushMsg.getContent();
            String phoneNum = kafkaPushMsg.getPhoneNum();
            if (StringUtils.isBlank(companyId) || StringUtils.isBlank(userId) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
                log.info("【push信息】eia推送消息推送失败，缺少必要参数:{}", JsonUtils.toJson(kafkaPushMsg));
                return;
            }
            //查询企业授权信息
            LanxinCorp lanxinCorp = lanxinCorpDao.selectByCompanyId(companyId);
            if (lanxinCorp == null) {
                log.info("【push信息】eia推送该企业蓝信密钥不存在,companyId:{}", companyId);
                return;
            }
            OpenCompanySourceType openCompanySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(companyId);
            Integer clientType = openCompanySourceType.getClientType();
            if (!OpenCompanyClientType.H5.getType().equals(clientType)) {
                log.info("【push信息】eia蓝信推送消息推送失败，client_type不正确{}", JsonUtils.toJson(kafkaPushMsg));
                return;
            }
            String link = webappHost + String.format(LanXinConstant.LANXIN_EIA_APP_HOME, lanxinCorp.getAppId());
            link = link + java.net.URLEncoder.encode(MessagePushUtils.messageUrlTransfer(kafkaPushMsg, ""), "GBK").replaceAll("url%3D","url=");
            LanXinBaseDTO<LanXinUserStaffIdDTO> lanXinUserStaffIdDTOLanXinBaseDTO = lanXinService.getStaffidByPhone(lanxinCorp, phoneNum);
            if (ObjectUtils.isEmpty(lanXinUserStaffIdDTOLanXinBaseDTO)) {
                log.warn("【push信息】eia蓝信根据手机号获取staffId失败,companyId:{},phone:{} ", companyId, phoneNum);
                return;
            }
            LanXinMsgDTO lanXinMsgDTO = setMsg(lanXinUserStaffIdDTOLanXinBaseDTO.getData().getStaffId(), kafkaPushMsg, link);
            //判断消息是审批还是订单数据
            lanXinService.sendMsg(lanxinCorp, lanXinMsgDTO);
        }
    }

    private LanXinMsgDTO setMsg(String staffId, KafkaPushMsg kafkaPushMsg, String link) {
        return LanXinMsgDTO.builder()
                .userIdList(Arrays.asList(staffId))
                .msgType("linkCard")
                .msgData(LanXinMsgDTO.Msgdata.builder()
                        .linkCard(LanXinMsgDTO.Linkcard.builder()
                                .title(kafkaPushMsg.getTitle())
                                .description(kafkaPushMsg.getContent())
                                .link(link)
                                .fromName(DateUtils.toSimpleStr(new Date(), false))
                                .build())
                        .build())
                .build();
    }
}
