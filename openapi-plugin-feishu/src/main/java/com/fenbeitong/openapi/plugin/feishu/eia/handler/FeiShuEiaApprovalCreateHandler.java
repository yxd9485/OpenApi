package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.*;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  审批单创建
 *
 * @author lizhen
 */
@Component
@Slf4j
@Deprecated
public class FeiShuEiaApprovalCreateHandler implements ITaskHandler {
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    FeiShuEiaApprovalService feiShuEiaApprovalService;
    @Autowired
    ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    FeiShuEiaCarApprovalService feiShuEiaCarApprovalService;
    @Autowired
    FeiShuEiaTripApprovalService feiShuEiaTripApprovalService;
    @Autowired
    UserCenterService userCenterService;

    @Autowired
    private FeishuProcessApplyFactory applyFactory;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_EIA_APPROVAL_CREATE;
    }

    @Override
    public TaskResult execute(Task task) throws Exception {
        String dataId = task.getDataId();
        String corpId = task.getCorpId();
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
        }
        FeiShuApprovalRespDTO feiShuApprovalDetail = feiShuEiaApprovalService.getFeiShuApprovalDetail(corpId, dataId);
        FeiShuApprovalRespDTO.ApprovalData approvalData = feiShuApprovalDetail.getData();
        // 只处理分贝通的审批单
        String approvalCode = approvalData.getApprovalCode();
        log.info("根据审批单code查找分贝通审批单 {}", approvalCode);
        ThirdApplyDefinition apply = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(approvalCode);
        if (apply == null || !pluginCorpDefinition.getAppId().equals(apply.getAppId())) {
            log.info("非分贝通审批单, 跳过, approvalCode: {}", approvalCode);
            return TaskResult.ABORT;
        }
        IFeishuProcessApplyService processApply = applyFactory.getProcessApply(apply.getProcessType());
        return processApply.processApply(task, pluginCorpDefinition, apply, approvalData);
    }
}
