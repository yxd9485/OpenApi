package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.ProcessInstance;
import com.fenbeitong.openapi.plugin.wechat.eia.service.process.WeChatEiaProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Component
@Slf4j
public abstract class AbstractWeChatEiaEiaProcessApply extends AbstractCarApplyService  {
@Autowired
WeChatEiaProcessInstanceService weChatEiaProcessInstanceService;

    protected void saveDingtalkProcessInstance(Task task, ThirdApplyDefinition apply, WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo ) {
        // 记录审批实例信息
        ProcessInstance instance = new ProcessInstance();
        instance.setCorpId(task.getCorpId());
        instance.setTitle(weChatApprovalInfo.getTemplateId());
        instance.setBizAction("NONE");
        instance.setBusinessId(weChatApprovalInfo.getSpNo());
        instance.setInstanceId(task.getDataId());
        instance.setProcessCode(apply.getThirdProcessCode());
        instance.setUserId(weChatApprovalInfo.getApplyer().getUserId());
        log.info("保存审批单实例对象：{}", JsonUtils.toJson(instance));
        weChatEiaProcessInstanceService.saveProcessInstance(instance);
    }
}
