package com.fenbeitong.openapi.plugin.yunzhijia.process;

import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 云之家订单审批通过后，会发送消息通知，通过apply审批回调消息接收接口转发处理
 */
@Component
@Slf4j
public class YunzhijiaOrderProcessApply extends AbstractApplyService implements IYunzhijiaProcessApply {
    @Autowired
    OpenOrderApplyDao openOrderApplyDao;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    AuthDefinitionDao authDefinitionDao;
    @Value("${host.openplus}")
    private String openPlusHost;
    @Autowired
    private RestHttpUtils httpUtil;
    @Autowired
    CommonAuthService commonAuthService;
    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, ThirdApplyDefinition thirdApplyDefinition, PluginCorpDefinition apply, YunzhijiaApplyEventDTO.YunzhijiaApplyData yunzhijiaApplyData) {
        //1.接收到云之家消息回调通知，解析处理
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = commonApplyService.createFbtOrderApply(corpId, dataId, OpenType.YUNZHIJIA.getType());
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
