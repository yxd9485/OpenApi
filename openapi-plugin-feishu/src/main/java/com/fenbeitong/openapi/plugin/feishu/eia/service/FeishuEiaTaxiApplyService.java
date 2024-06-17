package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;


/**
 * 正向行程审批服务
 *
 * @author yan.pb
 * @date 2021/2/20
 */
@Slf4j
@ServiceAspect
@Service
@Deprecated
public class FeishuEiaTaxiApplyService implements IFeishuProcessApplyService {

    @Autowired
    private FeiShuEiaApprovalService feiShuEiaApprovalService;

    @Autowired
    private FeiShuEiaCarApprovalService feiShuEiaCarApprovalService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition pluginCorpDefinition, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        // 申请单提交人ID
        String userId = approvalData.getUserId();
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), userId);
        if (StringUtils.isNotBlank(ucToken)) {
            String form = approvalData.getForm();
            CommonApplyReqDTO commonApplyReqDTO = feiShuEiaApprovalService.parseFeiShuTripApprovalForm(pluginCorpDefinition.getAppId(), corpId, dataId, form, 12 , userId);
            CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
            CreateApplyRespDTO feiShuCarApprove = feiShuEiaCarApprovalService.createFeiShuCarApprove(ucToken, carApproveCreateReqDTO);
            if (ObjectUtils.isEmpty(feiShuCarApprove) || StringUtils.isBlank(feiShuCarApprove.getId())) {
                return TaskResult.FAIL;
            }
            return TaskResult.SUCCESS;
        }
        return TaskResult.FAIL;
    }
}
