package com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.service.impl;

import com.fenbeitong.openapi.plugin.support.common.constant.MsgEventType;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.dao.DingtalkMsgRecipientDao;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.entity.DingtalkMsgRecipient;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.service.IDingtalkMsgRecipientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/11/16
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkMsgRecipientServiceImpl implements IDingtalkMsgRecipientService {

    @Autowired
    private DingtalkMsgRecipientDao dingtalkMsgRecipientDao;

    @Override
    public String getMsgRecipientList(String corpId, MsgEventType msgEventType) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("corpId", corpId);
        condition.put("status", 1);
        if (msgEventType != null) {
            condition.put("eventTags", msgEventType.getValue());
        }
        List<DingtalkMsgRecipient> dingtalkMsgRecipients = dingtalkMsgRecipientDao.listDingtalkMsgRecipient(condition);
        List<String> userIdList = dingtalkMsgRecipients.stream().map(DingtalkMsgRecipient::getUserId).collect(Collectors.toList());
        String msgRecipientList = String.join(",", userIdList);
        return msgRecipientList;
    }
}
