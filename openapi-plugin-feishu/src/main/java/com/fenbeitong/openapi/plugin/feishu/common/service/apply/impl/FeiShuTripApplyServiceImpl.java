package com.fenbeitong.openapi.plugin.feishu.common.service.apply.impl;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeiShuTripApprovalService;
import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeishuProcessApplyService;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyProcessFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.FeiShuApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

/**
 * 差旅正向审批
 * @author xiaohai
 * @Date 2022/07/04
 */
@Slf4j
@ServiceAspect
@Service
public class FeiShuTripApplyServiceImpl  implements FeishuProcessApplyService {

    @Autowired
    private FeiShuApprovalService feiShuApprovalService;

    @Autowired
    private FeiShuTripApprovalService feiShuTripApprovalService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private OpenTripApplyServiceImpl openTripApplyService;


    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition pluginCorpDefinition, ThirdApplyDefinition apply, FeiShuApprovalRespDTO.ApprovalData approvalData) throws Exception {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //申请单提交人ID
        String userId = approvalData.getUserId();
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), userId);
        if (StringUtils.isNotBlank(ucToken)) {
            String form = approvalData.getForm();
            CommonApplyReqDTO commonApplyReqDTO = feiShuApprovalService.parseFeiShuTripApprovalForm(pluginCorpDefinition.getAppId(), corpId, dataId, form, 1 , userId);
            commonApplyReqDTO.getApply().setCompanyId(pluginCorpDefinition.getAppId());
            commonApplyReqDTO.getApply().setEmployeeId(userId);
            //判断是否是非行程
            if(ObjectUtils.isEmpty(commonApplyReqDTO.getMultiTrip())){
                TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
                CreateApplyRespDTO tripApproveRespDTO = feiShuTripApprovalService.createFeiShuTripApprove(ucToken, tripApproveCreateReqDTO);
                if (!ObjectUtils.isEmpty(tripApproveRespDTO) && !StringUtils.isBlank(tripApproveRespDTO.getId())) {
                    return TaskResult.SUCCESS;
                }

            }else{
                MultiTripApproveCreateReqDTO multiTripReq = buildMultiTrip(commonApplyReqDTO);
                String applyId = openTripApplyService.createMultiTripApply(ucToken, multiTripReq,task.getCorpId());
                if (!ObjectUtils.isEmpty(applyId)){
                    return TaskResult.SUCCESS;
                }

            }

        }
        return TaskResult.FAIL;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplyProcessFactory.registerHandler(ProcessTypeConstant.Trip , this );
    }




    public MultiTripApproveCreateReqDTO buildMultiTrip(CommonApplyReqDTO commonApplyReqDTO){
        MultiTripApproveCreateReqDTO multiTripInfo = new MultiTripApproveCreateReqDTO();
        multiTripInfo.setApply(buildMultiTripApply( commonApplyReqDTO));
        multiTripInfo.setTrip( commonApplyReqDTO.getMultiTrip());


        MultiTripDTO trip = multiTripInfo.getTrip();
        trip.setStartTime(commonApplyReqDTO.getMultiTrip().getStartTime());
        trip.setEndTime(commonApplyReqDTO.getMultiTrip().getEndTime());

        //金额转成分
        BigDecimal estimatedAmount = trip.getEstimatedAmount();
        if(estimatedAmount!=null){
            BigDecimal bigDecimal = BigDecimalUtils.yuan2fen(  BigDecimalUtils.obj2big( estimatedAmount ) );
            trip.setEstimatedAmount( bigDecimal );
        }else{
            trip.setEstimatedAmount( new BigDecimal(0) );
        }
        multiTripInfo.getApply().setBudget(trip.getEstimatedAmount());
        multiTripInfo.getApply().setCostAttributionList(null);

        return multiTripInfo;
    }

    private MultiTripApplyDTO buildMultiTripApply(CommonApplyReqDTO commonApplyReqDTO){
        MultiTripApplyDTO apply = new MultiTripApplyDTO();
        CommonApply commonApply = commonApplyReqDTO.getApply();
        apply.setThirdId( commonApply.getThirdId() );
        apply.setEmployeeId( commonApply.getEmployeeId() );
        apply.setApplyReason(commonApply.getApplyReason());
        apply.setApplyReasonDesc(commonApply.getApplyReasonDesc());
        apply.setCostAttributionList(commonApply.getCostAttributionList());
        apply.setTravelTimeList(commonApplyReqDTO.getApply().getTravelTimeList());
        apply.setTravelDay(commonApplyReqDTO.getApply().getTravelDay());
        return apply;
    }

}
