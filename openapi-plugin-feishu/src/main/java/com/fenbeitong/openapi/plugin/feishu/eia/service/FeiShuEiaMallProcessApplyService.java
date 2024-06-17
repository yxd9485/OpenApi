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
import org.springframework.stereotype.Component;

/**
 * 飞书采购审批处理
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@Component
@Slf4j
public class FeiShuEiaMallProcessApplyService implements IFeishuProcessApplyService {

    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition feishuCorp, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = commonApplyService.notifyMallApplyAgree(corpId, dataId, OpenType.FEISHU_EIA.getType());
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
