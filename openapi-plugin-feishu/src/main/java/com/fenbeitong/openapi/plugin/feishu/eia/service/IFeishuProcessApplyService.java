package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;

/**
 * 飞书审批
 *
 * @author yan.pb
 * @date 2021/2/19
 */
public interface IFeishuProcessApplyService {

    /**
     * 处理钉钉任务
     *
     * @param task         任务
     * @param feishuCorp   企业信息表
     * @param apply        审批单注册表
     * @param approvalData 审批单详情
     * @return 处理结果
     */
    TaskResult processApply(Task task, PluginCorpDefinition feishuCorp, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception;
}
