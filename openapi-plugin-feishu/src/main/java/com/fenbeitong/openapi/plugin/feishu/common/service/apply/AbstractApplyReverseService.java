package com.fenbeitong.openapi.plugin.feishu.common.service.apply;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.util.DateUtil;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * @author xiaohai
 * @date 2022/07/04
 */
@Slf4j
@ServiceAspect
@Service
public abstract class AbstractApplyReverseService {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    public TaskResult reverseProcessApply(Task task, String status , Integer openType) {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = false;
        if(FeiShuConstant.APPROVAL_INSTANCE_STATUS_APPROVED.equals(status)){
            //同意
            fbtOrderApply = commonApplyService.notifyAgree(corpId, dataId, openType);
        }else if(FeiShuConstant.APPROVAL_INSTANCE_STATUS_REJECTED.equals(status)){
            fbtOrderApply = commonApplyService.notifyThirdApplyRepulse(corpId, dataId, openType , getCategory() , "");
        }
        return fbtOrderApply ? TaskResult.SUCCESS : TaskResult.FAIL;
    }

    /**
     *
     * @param task
     * @param pluginCorpDefinition
     * @param apply
     * @param approvalData
     * @return
     */
    public TaskResult processApply(Task task, PluginCorpDefinition pluginCorpDefinition, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception{
        return null;
    }

    protected abstract String getCategory();

}
