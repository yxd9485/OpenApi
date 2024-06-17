package com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.dao.DingtalkCsmMsgRecipientDao;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity.DingtalkCsmMsgRecipient;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.service.IDingtalkCsmMsgRecipientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/11/12
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkCsmMsgRecipientServiceImpl implements IDingtalkCsmMsgRecipientService {

    @Autowired
    private DingtalkCsmMsgRecipientDao dingtalkCsmMsgRecipientDao;

    @Override
    public DingtalkCsmMsgRecipient getCsmMsgRecipient(String corpId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("clientCorpId", corpId);
        DingtalkCsmMsgRecipient dingtalkCsmMsgRecipient = dingtalkCsmMsgRecipientDao.getDingtalkCsmMsgRecipient(condition);
        return dingtalkCsmMsgRecipient;
    }
}
