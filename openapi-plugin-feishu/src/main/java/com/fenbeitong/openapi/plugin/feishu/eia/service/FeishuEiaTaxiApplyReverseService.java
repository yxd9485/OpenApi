package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 反向行程审批
 *
 * @author yan.pb
 * @date 2021/2/20
 */
@Slf4j
@ServiceAspect
@Service
public class FeishuEiaTaxiApplyReverseService implements IFeishuProcessApplyService {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition feishuCorp, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = commonApplyService.notifyTaxiApplyAgree(corpId, dataId, OpenType.FEISHU_EIA.getType());
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
