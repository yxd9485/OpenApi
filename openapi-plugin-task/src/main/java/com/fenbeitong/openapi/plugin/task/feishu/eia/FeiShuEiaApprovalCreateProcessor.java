package com.fenbeitong.openapi.plugin.task.feishu.eia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackApprovalDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyProcessFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyProcessReverseFactory;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeEnum;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
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
public class FeiShuEiaApprovalCreateProcessor extends AbstractTaskProcessor {

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private FeiShuEiaApprovalService feiShuEiaApprovalService;

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_EIA_APPROVAL_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        String dataId = task.getDataId();
        String corpId = task.getCompanyId();
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("企业不存在，任务丢弃 success");
        }
        FeiShuApprovalRespDTO feiShuApprovalDetail = feiShuEiaApprovalService.getFeiShuApprovalDetail(corpId, dataId);
        FeiShuApprovalRespDTO.ApprovalData approvalData = feiShuApprovalDetail.getData();
        // 只处理分贝通的审批单
        String approvalCode = approvalData.getApprovalCode();
        log.info("根据审批单code查找分贝通审批单 {}", approvalCode);
        ThirdApplyDefinition apply = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(approvalCode);
        if (apply == null || !pluginCorpDefinition.getAppId().equals(apply.getAppId())) {
            log.info("非分贝通审批单, 跳过, approvalCode: {}", approvalCode);
            return TaskProcessResult.success("非分贝通审批单, 跳过 success");
        }
        FeiShuCallbackApprovalDTO feiShuCallbackApprovalDTO = JsonUtils.toObj(task.getDataContent(), FeiShuCallbackApprovalDTO.class);
        String status = feiShuCallbackApprovalDTO.getEvent().getStatus();
        //判断审批是正向还是反向
        Integer processType = apply.getProcessType();
        String processDirType = ProcessTypeEnum.valueOf(processType);
        if (FeiShuConstant.APPROVAL_INSTANCE_STATUS_REJECTED.equals(status) && "0".equals(processDirType)) {
            log.info("不是反向审批的，拒绝任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("不是反向审批的，拒绝任务丢弃 success");
        }
        TaskResult taskResult ;
        if("1".equals(processDirType)){
            //反向审批
            taskResult = ApplyProcessReverseFactory.getStrategyMap(apply.getProcessType()).reverseProcessApply(FinhubTaskUtils.convert2Task(task), status, OpenType.FEISHU_EIA.getType());
        }else{
            //正向审批
            taskResult = ApplyProcessFactory.getStrategyMap(apply.getProcessType()).processApply(FinhubTaskUtils.convert2Task(task), pluginCorpDefinition, apply, approvalData);
        }
        return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
    }
}
