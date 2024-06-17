package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaApprovalService;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaCarApprovalService;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaTripApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeReqDTO;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 *  审批单创建
 *
 * @author lizhen
 */
@Component
@Slf4j
public class FeiShuEiaApprovalCancelHandler implements ITaskHandler {
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

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_EIA_APPROVAL_REVERTED;
    }

    @Override
    public TaskResult execute(Task task) throws Exception {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
        }
        //审批单详情信息
        FeiShuApprovalRespDTO feiShuApprovalDetail = feiShuEiaApprovalService.getFeiShuApprovalDetail(corpId, dataId);
        FeiShuApprovalRespDTO.ApprovalData approvalData = feiShuApprovalDetail.getData();
        //根据code查询是差旅审批还是用车审批
        String approvalCode = approvalData.getApprovalCode();
        ThirdApplyDefinition thirdApplyConfigByProcessCode = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(approvalCode);
        if (ObjectUtils.isEmpty(thirdApplyConfigByProcessCode)) {
            return TaskResult.ABORT;
        }
        //申请单提交人ID
        String userId = approvalData.getUserId();
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), userId);
        TripApproveChangeApply tripApproveChangeApply = new TripApproveChangeApply();
        tripApproveChangeApply.setCompanyId(pluginCorpDefinition.getAppId());
        if (StringUtils.isNotBlank(ucToken)) {
            TripApproveChangeReqDTO build = TripApproveChangeReqDTO.builder()
                    .applyId(dataId)
                    .thirdType(2)
                    .apply(tripApproveChangeApply)
                    .build();
            boolean bool = feiShuEiaTripApprovalService.cancelFeiShuTripApprove(ucToken, build);
            if (!bool) {
                return TaskResult.FAIL;
            }
            return TaskResult.SUCCESS;
        }
        return TaskResult.FAIL;
    }
}
