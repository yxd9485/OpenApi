package com.fenbeitong.openapi.plugin.task.feishu.eia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
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
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
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
public class FeiShuEiaApprovalCancelProcessor extends AbstractTaskProcessor {

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FeiShuEiaApprovalService feiShuEiaApprovalService;

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Autowired
    private FeiShuEiaCarApprovalService feiShuEiaCarApprovalService;

    @Autowired
    private FeiShuEiaTripApprovalService feiShuEiaTripApprovalService;

    @Autowired
    private UserCenterService userCenterService;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_EIA_APPROVAL_REVERTED.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("企业不存在，任务丢弃 success");
        }
        //审批单详情信息
        FeiShuApprovalRespDTO feiShuApprovalDetail = feiShuEiaApprovalService.getFeiShuApprovalDetail(corpId, dataId);
        FeiShuApprovalRespDTO.ApprovalData approvalData = feiShuApprovalDetail.getData();
        //根据code查询是差旅审批还是用车审批
        String approvalCode = approvalData.getApprovalCode();
        ThirdApplyDefinition thirdApplyConfigByProcessCode = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(approvalCode);
        if (ObjectUtils.isEmpty(thirdApplyConfigByProcessCode)) {
            return TaskProcessResult.success("abort success");
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
                return TaskProcessResult.fail("fail");
            }
            return TaskProcessResult.success("success");
        }
        return TaskProcessResult.fail("fail");
    }
}
