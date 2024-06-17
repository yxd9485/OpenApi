package com.fenbeitong.openapi.plugin.feishu.common.service.apply.impl;

import com.fenbeitong.finhub.common.constant.saas.ApplyOrderCategory;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.AbstractApplyReverseService;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeiShuCarApprovalService;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeishuProcessApplyService;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyProcessFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.FeiShuApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 差旅正向审批
 * @author xiaohai
 * @Date 2022/07/04
 */
@Slf4j
@ServiceAspect
@Service
public class FeiShuTaxiApplyServiceImpl extends AbstractApplyReverseService implements FeishuProcessApplyService {

    @Autowired
    private FeiShuApprovalService feiShuApprovalService;

    @Autowired
    private FeiShuCarApprovalService feiShuCarApprovalService;

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
            CommonApplyReqDTO commonApplyReqDTO = feiShuApprovalService.parseFeiShuTripApprovalForm(pluginCorpDefinition.getAppId(), corpId, dataId, form, 12 , userId);
            CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
            CreateApplyRespDTO feiShuCarApprove = feiShuCarApprovalService.createFeiShuCarApprove(ucToken, carApproveCreateReqDTO);
            if (ObjectUtils.isEmpty(feiShuCarApprove) || StringUtils.isBlank(feiShuCarApprove.getId())) {
                return TaskResult.FAIL;
            }
            return TaskResult.SUCCESS;

        }
        return TaskResult.FAIL;
    }


    @Override
    protected String getCategory() {
        return ApplyOrderCategory.TAXI.getCategory();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplyProcessFactory.registerHandler(ProcessTypeConstant.Car , this );
    }


}
