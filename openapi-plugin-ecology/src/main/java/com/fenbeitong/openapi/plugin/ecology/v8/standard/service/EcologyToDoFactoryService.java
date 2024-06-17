package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.finhub.common.constant.ApplyStatus;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.WebHookTaskStatusEnum;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 泛微不同流程处理
 * @Auther zhang.peng
 * @Date 2022/1/13
 */
@ServiceAspect
@Service
@Slf4j
public class EcologyToDoFactoryService {

    @Autowired
    private IEcologyToDoService ecologyToDoService;

    public boolean doEcologyToDo(WebHookOrderDTO webHookOrderDTO){
        boolean result = false;
        // 创建待办
        if (String.valueOf(ApplyStatus.PendingAudit.getValue()).equals(webHookOrderDTO.getProcessInstanceStatus())){
            result = ecologyToDoService.createEcologyToDo(webHookOrderDTO);
        }
        // 完成待办
        if (String.valueOf(ApplyStatus.Done.getValue()).equals(webHookOrderDTO.getProcessInstanceStatus()) || refuseApprove(webHookOrderDTO) || finisApprove(webHookOrderDTO)){
            result = ecologyToDoService.finishEcologyToDo(webHookOrderDTO);
        }
        // 删除待办
        if (String.valueOf(ApplyStatus.Backout.getValue()).equals(webHookOrderDTO.getProcessInstanceStatus())){
            result = ecologyToDoService.deleteEcologyToDo(webHookOrderDTO);
        }
        return result;
    }

    /**
     * 审批被拒绝,将审批人的待办设置为已办
     * @param webHookOrderDTO 待办数据
     * @return true 执行 , false 不执行
     */
    public boolean refuseApprove(WebHookOrderDTO webHookOrderDTO){
        // 状态是已驳回,并且有审批人的 (驳回状态还会给发起人发送消息,所以需要判断是否有审批人)
        return String.valueOf(ApplyStatus.Return.getValue()).equals(webHookOrderDTO.getProcessInstanceStatus()) && !StringUtils.isBlank(webHookOrderDTO.getApproverId());
    }

    /**
     * 审批完成,将审批人的待办设置为已办
     * @param webHookOrderDTO 待办数据
     * @return true 执行 , false 不执行
     */
    public boolean finisApprove(WebHookOrderDTO webHookOrderDTO){
        // 状态是已驳回,并且有审批人的 (驳回状态还会给发起人发送消息,所以需要判断是否有审批人)
        return WebHookTaskStatusEnum.AGREE.getType().equals(webHookOrderDTO.getTaskStatus());
    }

}
