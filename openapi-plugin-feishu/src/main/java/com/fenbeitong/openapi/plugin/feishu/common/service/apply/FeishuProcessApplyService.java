package com.fenbeitong.openapi.plugin.feishu.common.service.apply;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import org.springframework.beans.factory.InitializingBean;

/**
 * 飞书反向审批
 *
 * @author xiaohai
 * @date 2022/07/04
 */
public interface FeishuProcessApplyService extends InitializingBean {

    /**
     * 正向审批
     * @param task
     * @param pluginCorpDefinition
     * @param apply
     * @param approvalData
     * @return
     */
    TaskResult processApply(Task task, PluginCorpDefinition pluginCorpDefinition, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception;

}
