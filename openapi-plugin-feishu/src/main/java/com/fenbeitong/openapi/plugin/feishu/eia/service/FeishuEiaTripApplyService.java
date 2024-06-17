package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
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
 * <p>Title: DingtalkTripApplyServiceImpl</p>
 * <p>Description: 钉钉行程审批服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 10:31 AM
 */
@Slf4j
@ServiceAspect
@Service
public class FeishuEiaTripApplyService implements IFeishuProcessApplyService {

    @Autowired
    private FeiShuEiaApprovalService feiShuEiaApprovalService;

    @Autowired
    private FeiShuEiaTripApprovalService feiShuEiaTripApprovalService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition pluginCorpDefinition, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //申请单提交人ID
        String userId = approvalData.getUserId();
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), userId);
        if (StringUtils.isNotBlank(ucToken)) {
            String form = approvalData.getForm();
            CommonApplyReqDTO commonApplyReqDTO = feiShuEiaApprovalService.parseFeiShuTripApprovalForm(pluginCorpDefinition.getAppId(), corpId, dataId, form, 1,userId);
            commonApplyReqDTO.getApply().setCompanyId(pluginCorpDefinition.getAppId());
            TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
            CreateApplyRespDTO tripApproveRespDTO = feiShuEiaTripApprovalService.createFeiShuTripApprove(ucToken, tripApproveCreateReqDTO);
            if (ObjectUtils.isEmpty(tripApproveRespDTO) || StringUtils.isBlank(tripApproveRespDTO.getId())) {
                return TaskResult.FAIL;
            }
            return TaskResult.SUCCESS;
        }
        return TaskResult.FAIL;
    }
}
