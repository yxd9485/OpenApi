package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.OpenApiResponse;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.eia.service.openapi.WeChatEiaPluginCallOpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Slf4j
@Component
public class WeChatTripEiaEiaProcessApply extends AbstractWeChatEiaEiaProcessApply implements IWeChatEiaProcessApply {
    @Resource(name = "weChatEiaDefaultTripWeChatEiaProcessFormParser")
    IWeChatEiaProcessFormParser iWeChatEiaProcessFormParser;

    @Autowired
    WeChatEiaPluginCallOpenApiService weChatEiaPluginCallOpenApiService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition corp, ThirdApplyDefinition apply, WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo) {
        //根据审批类型进行不同处理,现阶段企业微信只有创建审批单，修改和撤销都没有
        ApprovalInfo approvalInfo = iWeChatEiaProcessFormParser.parse(corp.getAppId(), apply.getProcessType(), task.getDataId(), weChatApprovalInfo);
        if (approvalInfo == null) {
            log.info("不符合分贝通审批单创建规则， 标记为废弃任务, taskId: {}, processInstanceId: {}", task.getId(), task.getDataId());
            return TaskResult.FAIL;
        }
        // 调用OPENAPI创建审批单
        String companyId = corp.getAppId();
        weChatEiaPluginCallOpenApiService.createTripApprove(companyId, approvalInfo, weChatApprovalInfo.getApplyer().getUserId());
        //保存审批实例信息
        saveDingtalkProcessInstance(task, apply, weChatApprovalInfo);
        return TaskResult.SUCCESS;
    }
}
