package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeChatOrderEiaEiaProcessApply extends AbstractWeChatEiaEiaProcessApply implements IWeChatEiaProcessApply {
    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition corp, ThirdApplyDefinition apply, WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo) {
        //1.接收到云之家消息回调通知，解析处理
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = commonApplyService.createFbtOrderApply(corpId, dataId, OpenType.WECHAT_EIA.getType());
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
