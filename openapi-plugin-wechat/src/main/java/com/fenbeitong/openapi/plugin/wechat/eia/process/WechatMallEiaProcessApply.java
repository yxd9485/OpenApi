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

/**
 * 企业微信采购审批处理
 * @Auther zhang.peng
 * @Date 2021/5/11
 */
@Component
@Slf4j
public class WechatMallEiaProcessApply extends AbstractWeChatEiaEiaProcessApply implements IWeChatEiaProcessApply {

    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition corp, ThirdApplyDefinition apply, WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo) {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = commonApplyService.notifyMallApplyAgree(corpId, dataId, OpenType.WECHAT_EIA.getType());
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
